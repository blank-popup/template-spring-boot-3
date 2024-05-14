package com.example.template.setting.mybatis;

import org.springframework.stereotype.Component;

@Component
public class HandlerAuditing {
    private ProviderDateTime providerDateTime = ProviderCurrentDateTime.INSTANCE;
    private ProviderUser providerUser = ProviderCurrentAhaUserDetails.INSTANCE;

    public <T extends Auditable> void markCreated(T target) {
        this.touch(target, true);
    }

    public <T extends Auditable> void markUpdated(T target) {
        this.touch(target, false);
    }

    private <T extends Auditable> void touch(T target, boolean isNew) {
        this.touchAuditor(target, isNew);
        this.touchDate(target, isNew);
    }

    private <T extends Auditable> void touchAuditor(T auditable, boolean isNew) {
        if (isNew) {
            auditable.setCreatedBy(providerUser.getId());
        }
        auditable.setUpdatedBy(providerUser.getId());
    }

    private <T extends Auditable> void touchDate(T auditable, boolean isNew) {
        if (isNew) {
            auditable.setCreatedAt(providerDateTime.getNow());
        }
        auditable.setUpdatedAt(providerDateTime.getNow());
    }
}
