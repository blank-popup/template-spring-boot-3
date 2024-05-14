package com.example.template.domain.v0.sampleAha;

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
public class EntitySampleAha extends EntityAbstractBase implements Auditable<Long, LocalDateTime>, SoftDeletable<Long, LocalDateTime> {
    private Long id;
    private String foo;
    private Long bar;
    private Long status;

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
