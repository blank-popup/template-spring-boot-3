package com.example.template.setting.security;

import com.example.template.setting.common.user.EntityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FilterAuthentication extends OncePerRequestFilter {
    private final ProviderJwt providerJwt;
    private final RepositorySecurity repositorySecurity;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        log.info("Filter : method, requestURI, queryString : {}, {}, {}", method, requestURI, queryString);

        Authentication authentication = null;

        if ((method != null && contextPath != null && requestURI != null)
                && (requestURI.equals(contextPath + "/error") == false)) {
            String jwt = providerJwt.resolveJwt(request);
            if (StringUtils.hasText(jwt) == true) {
                Jws<Claims> information = providerJwt.getInformationOfJwtAccess(jwt);
                if (information != null) {
                    String id = information.getBody().getSubject();
                    String type = (String)information.getBody().get("type");
                    if ("access".equals(type) == true) {
                        authentication = providerJwt.getAuthentication(id);
                    }
                }
                else {
                    information = providerJwt.getInformationOfJwtRefresh(jwt);
                    if (information != null) {
                        String id = information.getBody().getSubject();
                        String type = (String)information.getBody().get("type");
                        if ("refresh".equals(type) == true) {
                            String jwtRefresh = providerJwt.getRedisAuthRefreshIdToken(id);
                            if (jwt != null && jwt.equals(jwtRefresh) == true) {
                                List<EntityUser> entityUser = repositorySecurity.selectUserDetailsById(Long.valueOf(id));
                                AhaUserDetails ahaUserDetails = AhaUserDetails.of(entityUser);

                                String ip = request.getRemoteAddr();
                                String userAgent = request.getHeader("User-Agent");
                                String tokenAccess = providerJwt.createJwtAccess(Long.valueOf(id), ip, userAgent);
                                if (providerJwt.setRedisAuthAccessIdUserTimeout(id, ahaUserDetails) == true) {
                                    authentication = new UsernamePasswordAuthenticationToken(ahaUserDetails, "", ahaUserDetails.getAuthorities());
                                    response.addHeader("tokenAccess", tokenAccess);
                                    response.addHeader("termsRemainingTokenAccess", String.valueOf(providerJwt.getRedisAuthAccessExpire(id)));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (authentication == null) {
            log.warn("There is no valid authentication");
        }
        else {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
