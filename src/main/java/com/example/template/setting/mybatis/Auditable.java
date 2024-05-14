package com.example.template.setting.mybatis;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.temporal.TemporalAccessor;

public interface Auditable<U, T extends TemporalAccessor> {
    @JsonIgnore
    U getCreatedBy();

    @JsonIgnore
    void setCreatedBy(U createdBy);

    @JsonIgnore
    T getCreatedAt();

    @JsonIgnore
    void setCreatedAt(T createdAt);

    @JsonIgnore
    U getUpdatedBy();

    @JsonIgnore
    void setUpdatedBy(U updatedBy);

    @JsonIgnore
    T getUpdatedAt();

    @JsonIgnore
    void setUpdatedAt(T updatedAt);
}
