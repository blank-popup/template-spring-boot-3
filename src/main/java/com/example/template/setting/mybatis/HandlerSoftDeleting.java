package com.example.template.setting.mybatis;

import org.springframework.stereotype.Component;

@Component
public class HandlerSoftDeleting {
    private ProviderDateTime providerDateTime = ProviderCurrentDateTime.INSTANCE;
    private ProviderUser provideruser = ProviderCurrentAhaUserDetails.INSTANCE;

    public <T extends SoftDeletable> void markDeleted(T target) {
        this.touchDeleted(target);
    }

    private <T extends SoftDeletable> void touchDeleted(T softDeletable) {
        if (softDeletable.isDeleted()) {
            softDeletable.setDeletedBy(provideruser.getId());
            softDeletable.setDeletedAt(providerDateTime.getNow());
        }
    }
}
