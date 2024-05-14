package com.example.template.setting.common.user;

import com.example.template.setting.mybatis.Auditable;
import com.example.template.setting.mybatis.SoftDeletable;
import com.example.template.setting.common.base.EntityAbstractBase;
import com.example.template.util.UtilJson;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EntityUser extends EntityAbstractBase implements Auditable<Long, LocalDateTime>, SoftDeletable<Long, LocalDateTime> {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Long idRole;
    private String nameRole;
    private Long status;

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
