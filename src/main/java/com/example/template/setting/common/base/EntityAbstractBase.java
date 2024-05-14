package com.example.template.setting.common.base;

import com.example.template.setting.mybatis.SoftDeletable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public abstract class EntityAbstractBase {
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private Long deletedBy;
    private LocalDateTime deletedAt;

    public void delete() {
        if (this instanceof SoftDeletable) {
            setDeleted(true);
        }
    }

    public void undelete() {
        if (this instanceof SoftDeletable) {
            setDeleted(false);
        }
    }
}
