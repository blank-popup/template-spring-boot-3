package com.example.template.setting.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
// import javax.servlet.http.Part;
import java.io.IOException;
import java.lang.reflect.Type;
// import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

@Slf4j
@ControllerAdvice
public class HandlerException implements RequestBodyAdvice {
    private static final ThreadLocal<Object> HOLDER_BODY = new ThreadLocal<>();

    public void logInformationRequest(Exception ex, WebRequest webRequest, HttpServletRequest httpServletRequest) throws ServletException, IOException {
        log.error("$$$$$ From WebRequest ==================");
        log.error("$$$$$ Context Path : {}", webRequest.getContextPath());
        log.error("$$$$$ Description true : {}", webRequest.getDescription(true));
        log.error("$$$$$ Description false : {}", webRequest.getDescription(false));
        Iterator<String> iterator;
        iterator = webRequest.getHeaderNames();
        while (iterator.hasNext()) {
            String nameHeader = iterator.next();
            String valueHeader = webRequest.getHeader(nameHeader);
            log.error("$$$$$ Header : {} : {}", nameHeader, valueHeader);
        }
        log.error("$$$$$ Locale : {}", webRequest.getLocale());
        iterator = webRequest.getParameterNames();
        while (iterator.hasNext()) {
            String nameParameter = iterator.next();
            String valueParameter = webRequest.getParameter(nameParameter);
            log.error("$$$$$ Header Parameter : {} : {}", nameParameter, valueParameter);
        }
        log.error("$$$$$ RemoteUser : {}", webRequest.getRemoteUser());
        log.error("$$$$$ UserPrincipal : {}", webRequest.getUserPrincipal());

        log.error("$$$$$ From HttpServletRequest ==================");
        log.error("$$$$$ Method : {}", httpServletRequest.getMethod());
        log.error("$$$$$ QueryString : {}", httpServletRequest.getQueryString());
        log.error("$$$$$ IP : {}", httpServletRequest.getRemoteAddr());
        log.error("$$$$$ Referrer : {}", httpServletRequest.getRemoteHost());
        log.error("$$$$$ URL : {}", httpServletRequest.getRequestURI());
        if (httpServletRequest.getUserPrincipal() != null) {
            log.error("$$$$$ Username : {}", httpServletRequest.getUserPrincipal().getName());
        }

        log.error("$$$$$ PathInfo : {}", httpServletRequest.getPathInfo());
        log.error("$$$$$ Context Path : {}", httpServletRequest.getContextPath());
        log.error("$$$$$ RemoteUser : {}", httpServletRequest.getRemoteUser());
        log.error("$$$$$ Auth Type : {}", httpServletRequest.getAuthType());
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (int ii = 0; ii < cookies.length; ++ii) {
                log.error("$$$$$ Cookie : {}", cookies[ii].toString());
                log.error("$$$$$ Cookie : {} : {}", cookies[ii].getName(), cookies[ii].getValue());
//                log.error("$$$$$ Cookie Comment, Domain, MaxAge : {} : {} : {}", cookies[ii].getComment(), cookies[ii].getDomain(), cookies[ii].getMaxAge());
//                log.error("$$$$$ Cookie Path, Secure, Version : {}, {}, {}", cookies[ii].getPath(), cookies[ii].getSecure(), cookies[ii].getVersion());
            }
        }
        else {
            log.error("$$$$$ Cookie is null");
        }
        Enumeration<String> headerNames =  httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String nameHeader = headerNames.nextElement();
            String valueHeader = httpServletRequest.getHeader(nameHeader);
            log.error("$$$$$ Header : {} : {}", nameHeader, valueHeader);
//            Integer intHeader = httpServletRequest.getIntHeader(nameHeader);
//            log.error("$$$$$ Header : {} : {} : {}", nameHeader, valueHeader, intHeader);
        }
//        httpServletRequest.getDateHeader();
//        log.error("$$$$$ HttpServletMapping : {}", httpServletRequest.getHttpServletMapping());
//        Collection<Part> parts = httpServletRequest.getParts();
//        while (parts.iterator().hasNext()) {
//            Part part = parts.iterator().next();
//            String namePart = part.getName();
//            log.error("$$$$$ name, Part : {}, {}", namePart, parts.iterator().next());
//        }

        log.error("$$$$$ PathTranslated : {}", httpServletRequest.getPathTranslated());
        log.error("$$$$$ QueryString : {}", httpServletRequest.getQueryString());
        log.error("$$$$$ RequestedSessionId : {}", httpServletRequest.getRequestedSessionId());
        log.error("$$$$$ RequestURI : {}", httpServletRequest.getRequestURI());
        log.error("$$$$$ RequestURL : {}", httpServletRequest.getRequestURL());
        log.error("$$$$$ ServletPath : {}", httpServletRequest.getServletPath());
        log.error("$$$$$ Session : {}", httpServletRequest.getSession());
//        log.error("$$$$$ TrailerFields : {}", httpServletRequest.getTrailerFields());
        log.error("$$$$$ UserPrincipal : {}", httpServletRequest.getUserPrincipal());

        log.error("$$$$$ From HOLDER_BODY ==================");
        Object objectBody = HOLDER_BODY.get();
        log.error("$$$$$ Body : {}", objectBody);
        log.error("$$$$$ Exception : {}", ex.toString());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(final Exception ex, final WebRequest webRequest, final HttpServletRequest httpServletRequest) throws Exception {
        logInformationRequest(ex, webRequest, httpServletRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        HOLDER_BODY.remove();
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        HOLDER_BODY.set(body);
        return body;
    }

    @Override
    public Object handleEmptyBody(@Nullable Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
