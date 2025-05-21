package com.User.controller;

import com.User.dto.UserDto;
import com.User.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @PostMapping("/saveUser")
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto user) {
        try {
            System.out.println("request user"+user);
            UserDto savedUser = userService.saveUser(user);
            System.out.println("final response"+savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserDto("Error saving user"));
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

    }

    @PutMapping("/updateRestriction")
    public ResponseEntity<?> updateUserRestriction(
            @RequestBody Map<String, Object> requestBody) {

        String enrollmentNumber = (String) requestBody.get("enrollmentNumber");
        Boolean isRestricted = (Boolean) requestBody.get("isRestricted");

        if (enrollmentNumber == null || isRestricted == null) {
            return ResponseEntity.badRequest().body("Missing required parameters");
        }

        try {
            userService.updateUserRestriction(enrollmentNumber, isRestricted);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
