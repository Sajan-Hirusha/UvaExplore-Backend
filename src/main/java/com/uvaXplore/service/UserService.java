package com.uvaXplore.service;

import com.uvaXplore.dto.UserDto;
import com.uvaXplore.entity.User;
import com.uvaXplore.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDto saveUser(UserDto userDto) {
        try {

            String hashedPassword = passwordEncoder.encode(userDto.getPassword());
            System.out.println("hash password"+hashedPassword);
            userDto.setPassword(hashedPassword);
            System.out.println("after hashing"+userDto);
            userRepository.save(modelMapper.map(userDto, User.class));
            return userDto;
        } catch (Exception e) {
            System.out.println("error" + e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public List<UserDto> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            // Convert list of User entities to list of UserDto
            return users.stream()
                    .map(user -> modelMapper.map(user, UserDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("error: " + e);
            throw new RuntimeException("Failed to get users", e);
        }
    }

    public void updateUserRestriction(String enrollmentNumber, boolean isRestricted) {
        User user = userRepository.findById(enrollmentNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsRestricted(isRestricted);
        userRepository.save(user);
    }
}
