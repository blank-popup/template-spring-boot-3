package com.example.template.setting.exception;

public class BusinessException extends RuntimeException {
    private final String details;
    private final CodeException codeException;

    public BusinessException(String details, CodeException codeException) {
        super(codeException.getMessage());
        this.codeException = codeException;
        this.details = details;
    }

    public BusinessException(CodeException codeException) {
        super(codeException.getMessage());
        this.codeException = codeException;
        this.details = null;
    }

    public CodeException getExceptionCode() {
        return codeException;
    }

    public String getDetails() {
        return details;
    }
}
