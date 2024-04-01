package com.example.template.function.user;

import lombok.Data;

import java.time.LocalDateTime;

public class AhaUser {
    @Data
    public static class Getting {
        private String username;
    }

    @Data
    public static class Gotten {
        private Long id;
        private String username;
        private String password;
        private String email;
        private String phone;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class Creating {
        private Long id;
        private String username;
        private String password;
        private String email;
        private String phone;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class Created {
        private Integer status;
        private String message;
        private Integer count;
    }

    @Data
    public static class Putting {
        private Long id;
        private String username;
        private String password;
        private String email;
        private String phone;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class Put {
        private Integer status;
        private String message;
        private Integer count;
    }

    @Data
    public static class Patching {
        private Long id;
        private String username;
        private String password;
        private String email;
        private String phone;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class Patched {
        private Integer status;
        private String message;
        private Integer count;
    }

    @Data
    public static class Removing {
        private String username;
    }

    @Data
    public static class Removed {
        private Integer status;
        private String message;
        private Integer count;
    }
}
