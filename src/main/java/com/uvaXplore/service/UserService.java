package com.uvaXplore.service;

import com.uvaXplore.dto.UserDto;
import com.uvaXplore.entity.User;
import com.uvaXplore.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private ModelMapper modelMapper;

    public UserDto saveUser(UserDto userDto) {
        try {
            System.out.println("UserService"+userDto);
            userRepository.save(modelMapper.map(userDto, User.class));
            System.out.println("After Repo");
            return userDto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }
}
