package com.example.template.function.user;

import com.example.template.util.OID;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceUser {
    private final MapperUser mapperUser;
    private final PasswordEncoder passwordEncoder;
    @Value("${directory.user.image}")
    private String directoryUserImage;

    private boolean verifyNotEmptyUsernamePassword(String username, String password) {
        if (username == null || "".equals(username) == true || password == null || "".equals(password) == true) {
            return false;
        }

        return true;
    }

    public ResponseEntity<?> getUser(AhaUser.Getting getting) {
        AhaUser.Gotten gotten = mapperUser.getUser(getting);
        return ResponseEntity
                .ok()
                .body(gotten);
    }

    @Transactional
    public ResponseEntity<?> createUser(AhaUser.Creating creating) {
        String username = creating.getUsername();
        String password = creating.getPassword();
        if (verifyNotEmptyUsernamePassword(username, password) == false) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("message", "Invalid username or password");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(map);
        }

        creating.setPassword(passwordEncoder.encode(creating.getPassword()));
        int count = mapperUser.createUser(creating);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("count", count);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(map);
    }

    @Transactional
    public ResponseEntity<?> putUser(AhaUser.Putting putting) {
        String username = putting.getUsername();
        String password = putting.getPassword();
        if (verifyNotEmptyUsernamePassword(username, password) == false) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("message", "Invalid username or password");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(map);
        }

        putting.setPassword(passwordEncoder.encode(putting.getPassword()));
        int count = mapperUser.putUser(putting);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("count", count);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(map);
    }

    @Transactional
    public ResponseEntity<?> patchUser(AhaUser.Patching patching) {
        String username = patching.getUsername();
        String password = patching.getPassword();
        if (verifyNotEmptyUsernamePassword(username, password) == false) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("message", "Invalid username or password");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(map);
        }

        patching.setPassword(passwordEncoder.encode(patching.getPassword()));
        int count = mapperUser.patchUser(patching);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("count", count);
        if (count > 0) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(map);
        }

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(map);
    }

    @Transactional
    public ResponseEntity<?> removeUser(AhaUser.Removing removing) {
        int count = mapperUser.removeUser(removing);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("count", count);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(map);
    }

    public void downloadUserImage(HttpServletResponse response, AhaUserImage.Downloading downloading) {
        try {
            String path = directoryUserImage + "/" + downloading.getFilenameServer();

            File file = new File(path);
            if (file.exists() == false || file.isDirectory() == true) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return;
            }

            AhaUserImage.Downloaded fileDownload = mapperUser.getFileDownload(downloading);
            if (fileDownload == null) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return;
            }
            String filenameClient = fileDownload.getFilenameClient();
            if (filenameClient == null || "".equals(filenameClient) == true) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return;
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + filenameClient);

            FileInputStream fileInputStream = new FileInputStream(path);
            OutputStream out = response.getOutputStream();

            int read = 0;
            byte[] buffer = new byte[4096];
            while ((read = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            fileInputStream.close();

        } catch (Exception e) {
            log.error("Exception: {}", e.toString());
        }
    }

    @Transactional
    public ResponseEntity<?> uploadUserImage(AhaUserImage.Uploading uploading, MultipartFile file) {
        log.info("UserImage.Uploading : {}", uploading);
        String filenameClient = file.getOriginalFilename();
        log.info("file.getOriginalFilename() : {}", filenameClient);

        String filenameServer = OID.generateType1UUID().toString();
        log.info("filenameServer : {}", filenameServer);

        uploading.setFilenameServer(filenameServer);
        uploading.setFilenameClient(filenameClient);

        try {
            Path directoryUpdate = Paths.get(directoryUserImage);
            Files.createDirectories(directoryUpdate);
            Path targetPath = directoryUpdate.resolve(filenameServer).normalize();
            log.info("targetPath : {}", targetPath);
            file.transferTo(targetPath);
        } catch (Exception e) {
            log.warn("Exception uploadUserImage: {}", e.getMessage());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("message", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(map);
        }

        int count = mapperUser.createUserImage(uploading);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("count", count);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(map);
    }
}
