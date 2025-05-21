package com.User.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String enrollmentNumber;
    private String name;
    private String email;
    private String role;
    private String phoneNumber;
    private String password;
    private boolean isRestricted = false;


    public UserDto(String errorMessage) {
        this.name = errorMessage;
    }
}

