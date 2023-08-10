package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.DoctorRepository;
import com.example.registrationlogindemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AuthController {

    private UserService userService;
    private DoctorRepository doctorRepository;

    public AuthController(UserService userService, DoctorRepository doctorRepository) {
        this.userService = userService;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/index")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByEmail(username); // Assuming email is used as the username
        Long userId = user.getId(); // Here you have the ID of the currently logged-in user
        // You can use this userId in any subsequent operations or pass it to other views as needed.
        model.addAttribute("userId", userId);
        return "index";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        //save the user
        userService.saveUser(user);

        // Check if the user is a doctor
        if (user.getIsDoctor() != null && user.getIsDoctor()) {
            // Create a new Doctor instance
            Doctor doctor = new Doctor();
            doctor.setSpecialty(user.getDoc_specialty());
            doctor.setUser(userService.findUserByEmail(user.getEmail()));

            // Save the doctor entity
            doctorRepository.save(doctor);
        }

        model.addAttribute("success", true);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable (value="id") Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "edit";
    }

    @PostMapping("/users/{id}")
    public String editUser(@PathVariable("id") Long id,
                           @ModelAttribute("user") User user){

        //get user from database by id
        User existingUser = userService.getUserById(id);

        // Update the properties of the existing user
        existingUser.setLastName(user.getLastName());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAmka(user.getAmka());
        existingUser.setPhNo(user.getPhNo());

        //save updated user object
        userService.editUser(existingUser);
        return "redirect:/users";
    }

    //handler method to handle delete user request
    @GetMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        //session.setAttribute("msg","Ο χρήστης διαγράφτηκε επιτυχώς!");
        return "redirect:/users";
    }

    @GetMapping("/403")
    public String error403(){
        return "custom-403";
    }

}
