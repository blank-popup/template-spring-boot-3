package com.example.template.setting.security;

import com.example.template.util.InfoRequest;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProviderJwt implements InitializingBean {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.token-validity-ms}")
    private long tokenValidMillisecond;
     private final ServiceUserDetails serviceUserDetails;
//    private final ServiceRedis serviceRedis;

    @Override
    public void afterPropertiesSet() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createJwt(Long id, String ip, String userAgent) {
        Claims claims = Jwts.claims().setSubject(Long.toString(id));
        claims.put("ip", ip);
        claims.put("userAgent", userAgent);
        for (String key : claims.keySet()) {
            log.info("claims : [{}] : [{}]", key, claims.get(key));
        }
        Date now = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        log.info("Token : {}", token);

        return token;
    }

    // public Authentication getAuthentication(Jws<Claims> information) {
    //     log.info("JWT authentication : ID : {}", information.getBody().getSubject());
    //     UserDetails userDetails = userDetailsService.loadUserByUsername(information.getBody().getSubject());

    //     return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    // }

    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = serviceUserDetails.loadUserByUsername(jwt);
        if (userDetails == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveJwt(HttpServletRequest request) {
        String valueAuthorization = request.getHeader("Authorization");

        if (StringUtils.hasText(valueAuthorization) && valueAuthorization.startsWith("Bearer ")) {
            return valueAuthorization.substring(7);
        }

        return null;
    }

    public Jws<Claims> getInformationOfJwt(String jwt) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ipRemote = InfoRequest.getRemoteIP(request);
            String userAgentRemote = request.getHeader("User-Agent");

            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
            String ipJwt = (String)claims.getBody().get("ip");
            String userAgentJwt = (String)claims.getBody().get("userAgent");
            log.debug("Validate Tocken ipRemote : [{}], ipJwt : [{}]", ipRemote, ipJwt);
            log.debug("Validate Tocken userAgentRemote : [{}], userAgentJwt : [{}]", userAgentRemote, userAgentJwt);

            if (ipRemote != null && ipJwt != null) {
                if (ipRemote.equals(ipJwt) == false) {
                    log.warn("API Key : Invalid remote IP");
                    return null;
                }
            }
            if (userAgentRemote != null && userAgentJwt != null) {
                if (userAgentRemote.equals(userAgentJwt) == false) {
                    log.warn("API Key : Invalid User-Agent");
                    return null;
                }
            }
            if (claims.getBody().getExpiration().before(new Date()) == true) {
                log.warn("API Key : Invalid terms");
                return null;
            }

            return claims;
        } catch (SignatureException | MalformedJwtException | IllegalArgumentException exception) {
            log.warn("Invalid JWT");
        } catch (ExpiredJwtException exception) {
            log.warn("Expired JWT");
        } catch (UnsupportedJwtException exception) {
            log.warn("Not supported JWT");
        }

        return null;
    }
}
