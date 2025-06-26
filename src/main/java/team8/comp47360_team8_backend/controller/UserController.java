package team8.comp47360_team8_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.service.UserService;

import java.net.URI;

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
    public ResponseEntity<Void> createUser(@RequestBody User user)
    {
        User createdUser = userService.createUser(user);
        URI uri = ServletUriComponentsBuilder.fromUriString("/users/{userName}").buildAndExpand(createdUser.getUserName())
                .toUri();
        return ResponseEntity.created(uri).build();
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
