package com.uvaXplore.service;

import com.uvaXplore.dto.UserDto;
import com.uvaXplore.entity.User;
import com.uvaXplore.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private ModelMapper modelMapper;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public UserDto saveUser(UserDto userDto) {
        try {

//            String hashedPassword = passwordEncoder.encode(userDto.getPassword());
//            System.out.println("hash password"+hashedPassword);
//            userDto.setPassword(hashedPassword);
//            System.out.println("after hashing"+userDto);
            userRepository.save(modelMapper.map(userDto, User.class));
            return userDto;
        } catch (Exception e) {
            System.out.println("error" + e);
            throw new RuntimeException("Failed to save user", e);
        }
    }
}
