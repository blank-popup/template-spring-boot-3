package com.example.template.setting.security;

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
        private String token;
        private List<AhaAuth.Role> roles;
        private String refreshToken;
    }

    @Data
    public static class Role {
        private Long id;
        private String name;
    }

    @Data
    public static class UserRole {
        private Long id;
        private Long idUser;
        private Long idRole;
    }
}
