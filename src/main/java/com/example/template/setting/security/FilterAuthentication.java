package com.example.template.setting.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class FilterAuthentication extends OncePerRequestFilter {
    private final ProviderJwt providerJwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("\nExecute Order :\n1. FilterAuthentication.doFilter\n2. UserDetailsServiceCustom.loadUserByUsername\n3. UserDetailsCustom.getAuthorities\n4. AuthorizationDynamic.check");
        while (true) {
            String method = request.getMethod();
            String contextPath = request.getContextPath();
            String requestURI = request.getRequestURI();
            if (method == null || (contextPath != null && requestURI != null && requestURI.equals(contextPath + "/error")) == true) {
                break;
            }
            log.debug("method : contextPath : requestURI : {}, {}, {}", method, contextPath, requestURI);

            String jwt = providerJwt.resolveJwt(request);
            if (StringUtils.hasText(jwt)) {
                Jws<Claims> information = providerJwt.getInformationOfJwt(jwt);
                if (information == null) {
                    break;
                }

                Authentication authentication = providerJwt.getAuthentication(jwt);
                if (authentication == null) {
                    break;
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);
                break;
            }

            log.warn("There is no valid authentication : {}", requestURI);
            break;
        }

        filterChain.doFilter(request, response);
    }
}
