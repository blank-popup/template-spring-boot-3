package com.example.template.setting.mybatis;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.temporal.TemporalAccessor;

public interface SoftDeletable<U, T extends TemporalAccessor> {
    @JsonIgnore
    boolean isDeleted();

    @JsonIgnore
    U getDeletedBy();

    @JsonIgnore
    void setDeletedBy(U deletedBy);

    @JsonIgnore
    T getDeletedAt();

    @JsonIgnore
    void setDeletedAt(T deletedAt);

    @JsonIgnore
    void delete();

    @JsonIgnore
    void undelete();
}
