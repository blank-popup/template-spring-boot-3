package com.example.template.setting.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseException {
    private int status;
    private String code;
    private String message;
    protected String details;

    private ResponseException(final ResponseCode code) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.details = null;
    }

    private ResponseException(final ResponseCode code, final String details) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.details = details;
    }

    public static ResponseException of(final ResponseCode code) {
        return new ResponseException(code);
    }

    public static ResponseException of(final ResponseCode code, final String details) {
        return new ResponseException(code, details);
    }
}
