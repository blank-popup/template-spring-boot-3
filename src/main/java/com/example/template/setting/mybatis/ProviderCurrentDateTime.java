package com.example.template.setting.mybatis;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

public enum ProviderCurrentDateTime implements ProviderDateTime {
    INSTANCE;

    public TemporalAccessor getNow() {
        return LocalDateTime.now();
    }
}
