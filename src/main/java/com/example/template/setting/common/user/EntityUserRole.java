package com.example.template.setting.common.user;

import com.example.template.util.UtilJson;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntityUserRole {
    private Long id;
    private Long idUser;
    private Long idRole;

    @Builder
    private EntityUserRole(Long id, Long idUser, Long idRole) {
        this.id = id;
        this.idUser = idUser;
        this.idRole = idRole;
    }

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
