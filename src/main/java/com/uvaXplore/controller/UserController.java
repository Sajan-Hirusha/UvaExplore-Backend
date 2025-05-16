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



@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
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
                    .body(new UserDto("Error saving user"));  // Send an error message if save fails
        }
    }


}
