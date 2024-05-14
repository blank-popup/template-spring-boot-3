package com.example.template.setting.mybatis;

public interface ProviderUser<T, ID> {
    T getUser();
    ID getId();
}
