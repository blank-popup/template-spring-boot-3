package com.example.template.domain.v0.sampleAha;

import com.example.template.setting.exception.BusinessException;
import com.example.template.setting.exception.CodeException;

public class ExceptionSampleAha extends BusinessException {
    public ExceptionSampleAha(String details, CodeException codeException) {
        super(details, codeException);
    }

    public ExceptionSampleAha(String details) {
        super(details, CodeException.INVALID_INPUT_VALUE);
    }

    public ExceptionSampleAha(CodeException codeException) {
        super(codeException);
    }
}
