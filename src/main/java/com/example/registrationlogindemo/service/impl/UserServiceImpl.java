package com.example.registrationlogindemo.service.impl;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.Role;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.DoctorRepository;
import com.example.registrationlogindemo.repository.RoleRepository;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private DoctorRepository doctorRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.doctorRepository=doctorRepository;
    }

    @Override
    @Transactional
    public void saveUser(User userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhNo(userDto.getPhNo());
        user.setAmka(userDto.getAmka());

        //encrypt the password once we integrate spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        user.setIsDoctor(userDto.getIsDoctor());
        user.setDoc_specialty(userDto.getDoc_specialty());
        user.setStatus(userDto.getStatus());

        // Set the default role as "PATIENT"
        Role role = roleRepository.findByName("PATIENT");
        if(role==null){
            role = checkRoleExist();
        }

        // Check if the user is a doctor and set the role as "DOCTOR" if true
        if (user.getIsDoctor() != null && user.getIsDoctor().booleanValue()) {
            Role doctorRole = roleRepository.findByName("DOCTOR");
            if (doctorRole == null) {
                doctorRole = checkDoctorRoleExist();
            }
            user.setRoles(Collections.singletonList(doctorRole));
        } else {
            user.setRoles(Collections.singletonList(role));
        }

        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getPendingUsers() {
        // Call the custom query method to fetch pending users
        return userRepository.findPendingUsers();
    }



    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }
    private UserDto convertEntityToDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhNo(user.getPhNo());
        userDto.setEmail(user.getEmail());
        userDto.setAmka(user.getAmka());
        userDto.setIsDoctor(user.getIsDoctor());
        userDto.setDoc_specialty(user.getDoc_specialty());
        userDto.setStatus(user.getStatus());
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("PATIENT");
        return roleRepository.save(role);
    }

    private Role checkDoctorRoleExist(){
        Role role = new Role();
        role.setName("DOCTOR");
        return roleRepository.save(role);
    }
    @Override
    public User editUser(User user){
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateUserStatusByEmail(String email, String newStatus) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            user.setStatus(newStatus);
            userRepository.save(user);
        } else {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
    }


}
