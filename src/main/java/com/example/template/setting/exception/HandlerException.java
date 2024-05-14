package com.example.template.setting.exception;

import com.example.template.setting.body.WrapperHttpServletRequest;
import com.example.template.setting.mybatis.ProviderCurrentAhaUserDetails;
import com.example.template.setting.mybatis.ProviderUser;
import com.example.template.setting.security.AhaUserDetails;
import com.example.template.util.UtilJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
//@ControllerAdvice("com.example.template")
public class HandlerException {
    private ProviderUser<AhaUserDetails, Long> providerUser = ProviderCurrentAhaUserDetails.INSTANCE;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ResponseException> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleMaxUploadSizeExceededException", e, webRequest, httpServletRequest);
        final ResponseException response = ResponseException.of(CodeException.PAYLOAD_TOO_LARGE);
        return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(SizeLimitExceededException.class)
    protected ResponseEntity<ResponseException> handleSizeLimitExceededException(
            SizeLimitExceededException e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleSizeLimitExceededException", e, webRequest, httpServletRequest);
        final ResponseException response = ResponseException.of(CodeException.PAYLOAD_TOO_LARGE);
        return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ResponseException> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleMethodArgumentNotValidException", e, webRequest, httpServletRequest);
        final ResponseException response = ResponseException.of(CodeException.INVALID_INPUT_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ResponseException> handleBindException(BindException e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleBindException", e, webRequest, httpServletRequest);
        final ResponseException response = ResponseException.of(CodeException.INVALID_INPUT_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ResponseException> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleMethodArgumentTypeMismatchException", e, webRequest, httpServletRequest);
        final ResponseException response = ResponseException.of(CodeException.INVALID_TYPE_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ResponseException> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleHttpRequestMethodNotSupportedException", e, webRequest, httpServletRequest);
        ResponseException response = ResponseException.of(CodeException.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ResponseException> handleAccessDeniedException(AccessDeniedException e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleAccessDeniedException", e, webRequest, httpServletRequest);
        final ResponseException response = ResponseException.of(CodeException.HANDLE_ACCESS_DENIED);
        return new ResponseEntity<>(response,
                HttpStatus.valueOf(CodeException.HANDLE_ACCESS_DENIED.getStatus()));
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ResponseException> handleBusinessException(BusinessException e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleBusinessException", e, webRequest, httpServletRequest);
        final CodeException codeException = e.getExceptionCode();
        final ResponseException response = ResponseException.of(codeException, e.getDetails());
        return new ResponseEntity<>(response, HttpStatus.valueOf(codeException.getStatus()));
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ResponseException> handleRuntimeException(RuntimeException e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleRuntimeException", e, webRequest, httpServletRequest);
        final ResponseException response = ResponseException.of(CodeException.INVALID_INPUT_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseException> handleException(Exception e, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        logInformationRequest("handleException", e, webRequest, httpServletRequest);
        final ResponseException response = ResponseException.of(CodeException.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logInformationRequest(String title, Exception exception, WebRequest webRequest, HttpServletRequest httpServletRequest) {
        log.error(
                "\n================== Starting Information Exception : {} ==================\n"
                        + "Exception : {}\n"
                        + "User : {}({}) {}\n"
                        + "{} {}\n"
                        + "Request Header : {}\n"
                        + "Query String : {}\n"
                        + "Request Body : {}\n"
                        + "Cookie : {}\n"
                        + "==================================================\n",
                title,
                exception.toString(),
                providerUser.getUser().getUsername(), providerUser.getId(), httpServletRequest.getRemoteAddr(),
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI(),
                getHeaders(httpServletRequest),
                httpServletRequest.getQueryString(),
                getRequestBody(),
                getCookies(httpServletRequest)
        );
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

    private String getRequestBody() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        WrapperHttpServletRequest cachingRequest = (WrapperHttpServletRequest) request;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] body = cachingRequest.getContentAsByteArray();
            return objectMapper.readTree(body).toString();
        } catch (IOException e) {
            return null;
        }
    }

    private String getCookies(HttpServletRequest request) {
        Map<String, String> mapCookie = new HashMap<>();

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            mapCookie.put(cookie.getName(), cookie.getValue());
        }
        return UtilJson.convertObjectToJsonString(mapCookie);
    }
}
