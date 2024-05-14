package com.example.template.setting.security;

import com.example.template.setting.common.user.EntityUser;
import com.example.template.setting.common.user.EntityRole;
import com.example.template.setting.common.user.EntityUserRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceSecurity {
    private final RepositorySecurity repositorySecurity;
    private final ProviderJwt providerJwt;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AhaAuth.SignedUp signup(AhaAuth.SigningUp signingUp) {
        if (signingUp.getUsername() == null || "".equals(signingUp.getUsername()) == true
                || signingUp.getPassword() == null || "".equals(signingUp.getPassword()) == true) {
            throw new RuntimeException("Username or password is empty");
        }
        log.info("ROLES : {}", signingUp.getRoles());
        if (repositorySecurity.selectUserDetailsByUsername(signingUp.getUsername()).isEmpty() == false) {
            throw new RuntimeException("Username exist already");
        }

        signingUp.setPassword(passwordEncoder.encode(signingUp.getPassword()));
        if (repositorySecurity.createUser(signingUp) < 1) {
            throw new RuntimeException("Fail to insert User into DB");
        }

        List<EntityUser> newUser = repositorySecurity.selectUserDetailsByUsername(signingUp.getUsername());
        List<EntityRole> rolesRegistered = repositorySecurity.selectAllRole();
        List<EntityUserRole> userRoles = new ArrayList<>();
        List<String> rolesString = new ArrayList<>();
        for (String roleSiginingup : signingUp.getRoles()) {
            for (EntityRole roleRegitered : rolesRegistered) {
                String role = roleRegitered.getName();
                if (roleSiginingup != null && roleSiginingup.equals(role) == true) {
                    EntityUserRole userRole = EntityUserRole.builder()
                            .idUser(newUser.get(0).getId())
                            .idRole(roleRegitered.getId())
                            .build();
                    userRoles.add(userRole);
                    rolesString.add(roleRegitered.getName());
                }
            }
        }

        if (userRoles.isEmpty() == false) {
            if (repositorySecurity.createUserRole(userRoles) < 1) {
                throw new RuntimeException("Fail to insert UserRole into DB");
            }
        }

        AhaAuth.SignedUp signedUp = new AhaAuth.SignedUp();
        signedUp.setId(newUser.get(0).getId());
        signedUp.setUsername(signingUp.getUsername());
        signedUp.setRoles(rolesString);

        return signedUp;
    }

    public AhaAuth.SignedIn signin(AhaAuth.SigningIn signingIn) {
        List<EntityUser> entityUser = repositorySecurity.selectUserDetailsByUsername(signingIn.getUsername());
        AhaUserDetails ahaUserDetails = AhaUserDetails.of(entityUser);

        if (passwordEncoder.matches(signingIn.getPassword(), ahaUserDetails.getPassword()) == false) {
            throw new RuntimeException("Invalid username or password");
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        log.info("Client Information : IP Address : [{}]", ip);
        log.info("Client Information : User-Agent : [{}]", userAgent);

        String tokenAccess = providerJwt.createJwtAccess(ahaUserDetails.getId(), ip, userAgent);
        String tokenRefresh = providerJwt.createJwtRefresh(ahaUserDetails.getId(), ip, userAgent);

        Long idLong = ahaUserDetails.getId();
        String idString = String.valueOf(idLong);

        if (providerJwt.setRedisAuthAccessIdUserTimeout(idString, ahaUserDetails) == false) {
            throw new RuntimeException("Cannot write id and userDetails in Redis");
        }
        if (providerJwt.setRedisAuthRefreshIdTokenTimeout(idString, tokenRefresh) == false) {
            throw new RuntimeException("Cannot write id and token refresh in Redis");
        };

        AhaAuth.SignedIn signedIn = new AhaAuth.SignedIn();
        signedIn.setId(idLong);
        signedIn.setUsername(ahaUserDetails.getUsername());
        signedIn.setTokenAccess(tokenAccess);
        signedIn.setTermsRemainingTokenAccess(providerJwt.getRedisAuthAccessExpire(idString));
        signedIn.setTokenRefresh(tokenRefresh);
        signedIn.setTermsRemainingTokenRefresh(providerJwt.getRedisAuthRefreshExpire(idString));
        signedIn.setRoles(ahaUserDetails.getRoles());

        return signedIn;
    }
}
