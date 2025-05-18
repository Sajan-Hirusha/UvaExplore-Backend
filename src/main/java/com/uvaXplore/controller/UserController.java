package com.uvaXplore.controller;

import com.uvaXplore.dto.UserDto;
import com.uvaXplore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
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

    @PutMapping ("/updateRestriction/{encodedEnrollmentNumber}")
    public ResponseEntity<?> updateUserRestriction(
            @PathVariable String encodedEnrollmentNumber,
            @RequestBody Map<String, Boolean> restrictionMap) {
        System.out.println("Backend"+encodedEnrollmentNumber+restrictionMap);
        Boolean isRestricted = restrictionMap.get("isRestricted");
        if (isRestricted == null) {
            return ResponseEntity.badRequest().body("Missing isRestricted value");
        }

        try {
            userService.updateUserRestriction(encodedEnrollmentNumber, isRestricted);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
