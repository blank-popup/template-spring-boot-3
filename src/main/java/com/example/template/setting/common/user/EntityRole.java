package com.example.template.setting.common.user;

import com.example.template.util.UtilJson;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntityRole {
    private Long id;
    private String name;

    @Builder
    private EntityRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
