package com.example.template.setting.mybatis;

import java.time.temporal.TemporalAccessor;

public interface ProviderDateTime {
    TemporalAccessor getNow();
}
