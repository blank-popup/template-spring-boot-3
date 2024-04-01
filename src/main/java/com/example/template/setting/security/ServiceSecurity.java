package com.example.template.setting.security;

import com.example.template.setting.redis.ServiceRedis;
import com.example.template.util.InfoRequest;
import com.example.template.util.OID;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final MapperSecurity mapperSecurity;
    private final ProviderJwt providerJwt;
    private final PasswordEncoder passwordEncoder;
    private final ServiceRedis serviceRedis;
    @Value("${jwt.token-validity-ms}")
    private long tokenValidMillisecond;

    @Transactional
    public AhaAuth.SignedUp signup(AhaAuth.SigningUp signingUp) {
        if (signingUp.getUsername() == null || "".equals(signingUp.getUsername()) == true
                || signingUp.getPassword() == null || "".equals(signingUp.getPassword()) == true) {
            throw new RuntimeException("Username or password is empty");
        }
        log.info("ROLES : {}", signingUp.getRoles());
        if (mapperSecurity.selectUserDetailsByUsername(signingUp.getUsername()).isPresent() == true) {
            throw new RuntimeException("Username exist already");
        }

        signingUp.setPassword(passwordEncoder.encode(signingUp.getPassword()));
        mapperSecurity.createUser(signingUp);

        AhaUserDetails newUser = mapperSecurity.selectUserDetailsByUsername(signingUp.getUsername()).get();
        List<AhaAuth.Role> rolesRegistered = mapperSecurity.selectAllRole();
        List<String> roles = new ArrayList<>();
        for (String roleSigningUp : signingUp.getRoles()) {
            for (AhaAuth.Role roleRegistered : rolesRegistered) {
                String role = roleRegistered.getName();
                if (roleSigningUp != null && roleSigningUp.equals(role) == true) {
                    roles.add(role);
                }
            }
        }

        AhaAuth.SignedUp signedUp = new AhaAuth.SignedUp();
        signedUp.setId(newUser.getId());
        signedUp.setUsername(signingUp.getUsername());
        signedUp.setRoles(roles);

        return signedUp;
    }

    public AhaAuth.SignedIn signin(AhaAuth.SigningIn signingIn) {
        AhaUserDetails userDetails = mapperSecurity.selectUserDetailsByUsername(signingIn.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(signingIn.getPassword(), userDetails.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        List<AhaAuth.Role> roles = mapperSecurity.selectRolesByUsername(signingIn.getUsername());
        userDetails.setRoles(roles);
        String refreshToken = OID.generateType4UUID().toString();
        userDetails.setRefreshToken(refreshToken);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = InfoRequest.getRemoteIP(request);
        String userAgent = request.getHeader("User-Agent");
        log.info("Client Information : IP Address : [{}]", ip);
        log.info("Client Information : User-Agent : [{}]", userAgent);

        String token = providerJwt.createJwt(userDetails.getId(), ip, userAgent);

        serviceRedis.setAuthIdTokenTimeout(String.format("%d", userDetails.getId()), token, tokenValidMillisecond);
        serviceRedis.setAuthTokenUserTimeout(token, userDetails, tokenValidMillisecond);

        AhaAuth.SignedIn signedIn = new AhaAuth.SignedIn();
        signedIn.setId(userDetails.getId());
        signedIn.setUsername(userDetails.getUsername());
        signedIn.setToken(token);
        signedIn.setRoles(userDetails.getRoles());
        signedIn.setRefreshToken(refreshToken);

        return signedIn;
    }
}
