package com.example.clinicsystem.service;

import com.example.clinicsystem.dto.UserDto;
import com.example.clinicsystem.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void saveUser(User user);

    User findByEmail(String email);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    User getUserById(Long id);

    User editUser(User user);

    void deleteUser(Long id);

    void updateUserStatusByEmail(String email, String newStatus);

    List<User> getPendingUsers();

    Optional<User> findById(Long userId);

    void updateUserStatus(User user);

    void updateUser(User updatedUser);

    Long getUserIdByEmail(String username);
}
