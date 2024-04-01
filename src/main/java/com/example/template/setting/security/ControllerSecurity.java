package com.example.template.setting.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ControllerSecurity {
    private final ServiceSecurity serviceSecurity;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@RequestBody AhaAuth.SigningUp signingUp) {
        ResponseEntity<?> responseEntity = null;
        try {
            AhaAuth.SignedUp signedUp = serviceSecurity.signup(signingUp);

            responseEntity = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(signedUp);
        } catch (RuntimeException exception) {
            log.warn(exception.getMessage());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("message", exception.getMessage());
            responseEntity = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(map);
        }

        return responseEntity;
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<?> signin(@RequestBody AhaAuth.SigningIn signingIn) {
        ResponseEntity<?> responseEntity = null;
        try {
            AhaAuth.SignedIn signedIn = serviceSecurity.signin(signingIn);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "Bearer " + signedIn.getToken());

            responseEntity = ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(httpHeaders)
                    .body(signedIn);
        } catch (RuntimeException exception) {
            log.warn(exception.getMessage());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("message", exception.getMessage());
            responseEntity = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(map);
        }

        return responseEntity;
    }
}
