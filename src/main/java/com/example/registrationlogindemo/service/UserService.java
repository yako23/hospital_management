package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    User getUserById(Long id);

    User editUser(User user);

    void deleteUser(Long id);
}
