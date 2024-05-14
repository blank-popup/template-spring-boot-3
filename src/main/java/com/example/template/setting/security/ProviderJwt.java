package com.example.template.setting.security;

import com.example.template.setting.redis.ServiceRedis;
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
    @Value("${jwt.key-access}")
    private String jwtKeyAccess;
    @Value("${jwt.key-refresh}")
    private String jwtKeyRefresh;
    @Value("${jwt.term-s-token-access}")
    private long termsTokenAccess;
    @Value("${jwt.term-s-token-refresh}")
    private long termsTokenRefresh;
    @Value("${jwt.term-s-get-new-token-access}")
    private long termsNewTokenAccess;
    @Value("${jwt.term-s-get-new-token-refresh}")
    private long termsNewTokenRefresh;
    private final ServiceRedis serviceRedis;
    private final ServiceUserDetails serviceUserDetails;

    @Override
    public void afterPropertiesSet() {
        jwtKeyAccess = Base64.getEncoder().encodeToString(jwtKeyAccess.getBytes());
        jwtKeyRefresh = Base64.getEncoder().encodeToString(jwtKeyRefresh.getBytes());
    }

    private String createJwt(Long id, String ip, String userAgent, String type, Date now, Date expiration, String jwtKey) {
        Claims claims = Jwts.claims().setSubject(Long.toString(id));
        claims.put("ip", ip);
        claims.put("userAgent", userAgent);
        claims.put("type", type);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, jwtKey)
                .compact();

    }

    public String createJwtAccess(Long id, String ip, String userAgent) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + termsTokenAccess * 1000);
        String token = createJwt(id, ip, userAgent, "access", now, expiration, jwtKeyAccess);
        log.info("Token Access : {}", token);

        return token;
    }

    public String createJwtRefresh(Long id, String ip, String userAgent) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + termsTokenRefresh * 1000);
        String token = createJwt(id, ip, userAgent, "refresh", now, expiration, jwtKeyRefresh);
        log.info("Token Refresh : {}", token);

        return token;
    }

    public Authentication getAuthentication(String id) {
        if (id == null) {
            return null;
        }
        UserDetails userDetails = serviceUserDetails.loadUserByUsername(id);
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

    private boolean checkInformation(Jws<Claims> information) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ipRemote = request.getRemoteAddr();
            String userAgentRemote = request.getHeader("User-Agent");

            String ipJwt = (String)information.getBody().get("ip");
            String userAgentJwt = (String)information.getBody().get("userAgent");
            log.debug("Token ipRemote : [{}], ipJwt : [{}]", ipRemote, ipJwt);
            log.debug("Token userAgentRemote : [{}], userAgentJwt : [{}]", userAgentRemote, userAgentJwt);

            if (ipRemote != null && ipJwt != null) {
                if (ipRemote.equals(ipJwt) == false) {
                    log.warn("Inconsistent remote IP : JWT[" + ipJwt + "], Current[" + ipRemote + "]");
                    return false;
                }
            }
            if (userAgentRemote != null && userAgentJwt != null) {
                if (userAgentRemote.equals(userAgentJwt) == false) {
                    log.warn("Inconsistent User-Agent : JWT[" + userAgentJwt + "], Current[" + userAgentRemote + "]");
                    return false;
                }
            }
            if (information.getBody().getExpiration().before(new Date()) == true) {
                log.warn("JWT : Expiration time exceeded");
                return false;
            }

            return true;
    }

    private Jws<Claims> getInformationOfJwt(String jwt, String key) {
        try {
            Jws<Claims> information = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
            return information;
//            if (checkInformation(information) == true) {
//                return information;
//            }
//            else {
//                return null;
//            }
        } catch (SignatureException | MalformedJwtException | IllegalArgumentException exception) {
            log.warn("Invalid JWT");
        } catch (ExpiredJwtException exception) {
            log.warn("Expired JWT");
        } catch (UnsupportedJwtException exception) {
            log.warn("Not supported JWT");
        }

        return null;
    }

    public Jws<Claims> getInformationOfJwtAccess(String jwt) {
        return getInformationOfJwt(jwt, jwtKeyAccess);
    }

    public Jws<Claims> getInformationOfJwtRefresh(String jwt) {
        return getInformationOfJwt(jwt, jwtKeyRefresh);
    }

    public boolean setRedisAuthAccessIdUserTimeout(String id, AhaUserDetails ahaUserDetails) {
        return serviceRedis.setAuthAccessIdUserTimeout(id, ahaUserDetails, termsTokenAccess);
    }

    public AhaUserDetails getRedisAuthAccessIdUser(String id) {
        return serviceRedis.getAuthAccessIdUser(id);
    }

    public Long getRedisAuthAccessExpire(String id) {
        return serviceRedis.getAuthAccessExpire(id);
    }

    public boolean setRedisAuthRefreshIdTokenTimeout(String id, String token) {
        return serviceRedis.setAuthRefreshIdTokenTimeout(id, token, termsTokenRefresh);
    }

    public String getRedisAuthRefreshIdToken(String id) {
        return serviceRedis.getAuthRefreshIdToken(id);
    }

    public Long getRedisAuthRefreshExpire(String id) {
        return serviceRedis.getAuthRefreshExpire(id);
    }
}
