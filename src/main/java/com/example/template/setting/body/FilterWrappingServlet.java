package com.example.template.setting.body;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class FilterWrappingServlet extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        WrapperHttpServletRequest wrappingRequest = new WrapperHttpServletRequest(request);
        ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);
        chain.doFilter(wrappingRequest, wrappingResponse);
        wrappingResponse.copyBodyToResponse();
    }
}
