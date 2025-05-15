package com.uvaXplore.service;

import com.uvaXplore.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private ModelMapper modelMapper;
}
