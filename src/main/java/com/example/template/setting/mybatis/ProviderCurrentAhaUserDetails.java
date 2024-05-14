package com.example.template.setting.mybatis;

import com.example.template.setting.security.AhaUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public enum ProviderCurrentAhaUserDetails implements ProviderUser<AhaUserDetails, Long> {
    INSTANCE;

    @Override
    public AhaUserDetails getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AhaUserDetails) {
            return (AhaUserDetails) principal;
        } else {
            return AhaUserDetails.getAnonymousUser();
        }
    }

    @Override
    public Long getId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AhaUserDetails) {
            AhaUserDetails ahaUserDetails = (AhaUserDetails) principal;
            return ahaUserDetails.getId();
        } else {
            return AhaUserDetails.getAnonymousUser().getId();
        }
    }
}
