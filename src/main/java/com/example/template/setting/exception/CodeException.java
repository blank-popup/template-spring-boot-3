package com.example.template.setting.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CodeException implements ResponseCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "AHA-001", "Invalid Input Value"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST.value(), "AHA-002", "Invalid Type Value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "AHA-003", "Server Error"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "AHA-004", "Access is Denied"),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE.value(), "AHA-005", "Payload Too Large"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "AHA-006", "Method Not Allowed");

    private final int status;
    private final String code;
    private final String message;

    CodeException(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
