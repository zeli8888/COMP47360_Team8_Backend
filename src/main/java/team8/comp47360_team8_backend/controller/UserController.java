package team8.comp47360_team8_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.service.UserService;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 19:58
 * @Version : V1.0
 * @Description :
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/user")
                .build()
                .toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @PostMapping("/user/picture")
    public ResponseEntity<String> updateUserPicture(@RequestParam("file")MultipartFile file) {
        String pictureUrl = userService.updateUserPicture(file);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pictureUrl}").buildAndExpand(pictureUrl).toUri();
        return ResponseEntity.created(uri).body("Picture uploaded successfully");
    }

    @GetMapping("/user/picture/{filePath:.+}")
    public ResponseEntity<Resource> getUserPicture(@PathVariable String filePath){
        Resource userPicture = userService.getUserPicture(filePath);
        String contentType;
        try {
            contentType = Files.probeContentType(userPicture.getFile().toPath());
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + userPicture.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(userPicture);
    }

    @PutMapping("/user")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser() {
        return ResponseEntity.ok(userService.getUser());
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }
}
