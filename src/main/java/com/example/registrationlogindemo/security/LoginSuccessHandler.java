package com.example.registrationlogindemo.security;

import com.example.registrationlogindemo.dto.AppointmentDto;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.service.AppointmentService;
import com.example.registrationlogindemo.service.DoctorService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final DoctorService doctorService;

    public LoginSuccessHandler(UserRepository userRepository,
                               DoctorService doctorService) {
        this.userRepository = userRepository;
        this.doctorService = doctorService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // Retrieve the user's email from authentication
        String userEmail = authentication.getName();

        // Use the email to fetch the corresponding user from UserRepository
        User user = userRepository.findByEmail(userEmail);

        // Check if user is not null and has the userId
        if (user != null && user.getId() != null) {
            Long userId = user.getId();

        // Redirect the user based on their role
        if (roles.contains("ADMIN")) {
            response.sendRedirect("/welcome");
        } else if (roles.contains("PATIENT")) {
            response.sendRedirect("/appointment");
        } else {
            response.sendRedirect("/doctors");
        }
        } else {
            // Handle the case where user is not found or has no userId
            // You can redirect to an appropriate page or show an error message
            response.sendRedirect("/login?error");
        }
    }
}

