package com.example.template.function.user;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ControllerUser {
    private final ServiceUser serviceUser;

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUser(@PathVariable(value="username") String username) {
        AhaUser.Getting getting = new AhaUser.Getting();
        getting.setUsername(username);
        ResponseEntity<?> response = serviceUser.getUser(getting);
        return response;
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody AhaUser.Creating creating) {
        return serviceUser.createUser(creating);
    }

    @PutMapping("/user/{username}")
    public ResponseEntity<?> putUser(@PathVariable(value="username") String username, @RequestBody AhaUser.Putting putting) {
        putting.setUsername(username);
        return  serviceUser.putUser(putting);
    }

    @PatchMapping("/user/{username}")
    public ResponseEntity<?> patchUser(@PathVariable(value="username") String username, @RequestBody AhaUser.Patching patching) {
        patching.setUsername(username);
        return serviceUser.patchUser(patching);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> removeUser(@PathVariable(value="username") String username) {
        AhaUser.Removing removing = new AhaUser.Removing();
        removing.setUsername(username);
        return serviceUser.removeUser(removing);
    }

    @Value("${directory.user.image}")
    private String directoryUserImage;

    @GetMapping(value="/user-image/{filenameServer}")
    public void downloadUserImage(HttpServletResponse response, @PathVariable(value="filenameServer") String filenameServer) {
        AhaUserImage.Downloading downloading = new AhaUserImage.Downloading();
        downloading.setFilenameServer(filenameServer);
        serviceUser.downloadUserImage(response, downloading);
    }

    @PostMapping("/user-image")
    public ResponseEntity<?> uploadUserImage(AhaUserImage.Uploading uploading, @RequestPart(value="userImage", required = true) MultipartFile file) {
        return serviceUser.uploadUserImage(uploading, file);
    }
}
