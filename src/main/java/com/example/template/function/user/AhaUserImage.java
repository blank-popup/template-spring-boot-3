package com.example.template.function.user;

import lombok.Data;

public class AhaUserImage {
    @Data
    public static class Uploading {
        private Long id;
        private String description;
        private String filenameServer;
        private String filenameClient;
    }

    @Data
    public static class Uploaded {
        private Integer status;
        private String message;
    }

    @Data
    public static class Downloading {
        private String filenameServer;
    }

    @Data
    public static class Downloaded {
        private String filenameServer;
        private String filenameClient;
    }
}
