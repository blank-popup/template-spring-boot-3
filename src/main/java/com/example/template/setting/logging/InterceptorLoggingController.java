package com.example.template.setting.logging;

import com.example.template.setting.mybatis.ProviderCurrentAhaUserDetails;
import com.example.template.setting.mybatis.ProviderUser;
import com.example.template.setting.body.WrapperHttpServletRequest;
import com.example.template.setting.security.AhaUserDetails;
import com.example.template.util.UtilJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InterceptorLoggingController implements HandlerInterceptor {
    private ProviderUser<AhaUserDetails, Long> providerUser = ProviderCurrentAhaUserDetails.INSTANCE;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HandlerInterceptor.super.preHandle(request, response, handler);

        AhaUserDetails userInfo = providerUser.getUser();
        String username = userInfo.getUsername();

        if (isContentTypeJson(request.getContentType())) {
            log.debug(
                    "\n=== {} Request-{} ====\n"
                            + "{} {}\n"
                            + "Headers : {}\n"
                            + "RequestParam : {}\n"
                            + "RequestBody : {}\n"
                            + "==================================================\n",
                    username, request.getRequestedSessionId(),
                    request.getMethod(), request.getRequestURI(),
                    getHeaders(request),
                    getRequestParam(request),
                    getRequestBody(request));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);

        AhaUserDetails userInfo = providerUser.getUser();
        String username = userInfo.getUsername();

        if (isContentTypeJson(response.getContentType())) {
            log.debug(
                    "\n=== {} Response-{} ====\n"
                            + "{} {}\n"
                            + "HttpStatus : {}\n"
                            + "Headers : {}\n"
                            + "ResponseBody : {}\n"
                            + "==================================================\n",
                    username, request.getRequestedSessionId(),
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(),
                    getHeaders(response),
                    getResponseBody(response));
        }
    }

    private boolean isContentTypeJson(String contentType) {
        return contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private String getHeaders(HttpServletRequest request) {
        Map<String, String> mapHeader = new HashMap<>();

        Enumeration<String> namesHeader = request.getHeaderNames();
        while (namesHeader.hasMoreElements()) {
            String nameHeader = namesHeader.nextElement();
            mapHeader.put(nameHeader, request.getHeader(nameHeader));
        }
        return UtilJson.convertObjectToJsonString(mapHeader);
    }

    private String getHeaders(HttpServletResponse response) {
        Map<String, String> mapHeader = new HashMap<>();

        Collection<String> namesHeader = response.getHeaderNames();
        for (String nameHeader : namesHeader) {
            mapHeader.put(nameHeader, response.getHeader(nameHeader));
        }
        return UtilJson.convertObjectToJsonString(mapHeader);
    }

    private String getRequestParam(HttpServletRequest request) throws IOException {
        WrapperHttpServletRequest cachingRequest = (WrapperHttpServletRequest) request;
        return UtilJson.convertObjectToJsonString(cachingRequest.getParameterMap());
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        WrapperHttpServletRequest cachingRequest = (WrapperHttpServletRequest) request;
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(cachingRequest.getContentAsByteArray()).toString();
    }

    private String getResponseBody(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper cachingResponse = (ContentCachingResponseWrapper) response;
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(cachingResponse.getContentAsByteArray()).toString();
    }
}
