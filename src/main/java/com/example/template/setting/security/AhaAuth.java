package com.example.template.setting.security;

import com.example.template.setting.common.user.EntityRole;
//import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

public class AhaAuth {
    @Data
    public static class SigningUp {
        private String username;
        private String password;
        private List<String> roles;
    }

    @Data
    public static class SignedUp {
        private Long id;
        private String username;
        private List<String> roles;
    }

    @Data
    public static class SigningIn {
        private String username;
        private String password;
    }

    @Data
    public static class SignedIn {
        private Long id;
        private String username;
//        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private String tokenAccess;
//        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private String tokenRefresh;
//        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private Long termsRemainingTokenAccess;
//        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private Long termsRemainingTokenRefresh;
        private List<EntityRole> roles;
    }
}
