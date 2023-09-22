package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.DoctorRepository;
import com.example.registrationlogindemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               @RequestParam("isDoctor") Boolean isDoctor,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(userDto.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }

        // Convert UserDto to User
        User user = convertUserDtoToUser(userDto);

        // Set the status based on whether the user is a doctor or not
        String status = isDoctor ? "ΕΚΚΡΕΜΕΙ" : "ΕΝΕΡΓΟΣ";
        if (user.getDoc_specialty()==null){
            user.setIsDoctor(false);
        }

        user.setStatus(status);

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

    // Helper method to convert UserDto to User
    private User convertUserDtoToUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setAmka(userDto.getAmka());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setPhNo(userDto.getPhNo());
        user.setIsDoctor(userDto.getIsDoctor());
        user.setDoc_specialty(userDto.getDoc_specialty());
        user.setStatus(userDto.getStatus());
        return user;
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

    @GetMapping("/contact")
    public String contact(){
        return "contact";
    }
    @GetMapping("/welcome")
    public String welcome(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Get the user's role from UserDetails (e.g., authorities)
        String userRole = userDetails.getAuthorities().isEmpty() ? "" : userDetails.getAuthorities().iterator().next().getAuthority();

        String username = userDetails.getUsername();
        User user = userService.findByEmail(username);

        // Assuming User has properties firstName and lastName
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        UserDto userInfo = new UserDto();
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);

        // Add the userRole and userFullName to the model
        model.addAttribute("userRole", userRole);
        model.addAttribute("userInfo", userInfo);

        return "welcome_admin";
    }

    @GetMapping("/welcome/user")
    public String welcomeUser(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        User user = userService.findByEmail(username);

        // Assuming User has properties firstName and lastName
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        UserDto userInfo = new UserDto();
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);

        model.addAttribute("userInfo", userInfo);

        return "welcome_user";
    }

    @GetMapping("/welcome/doctor")
    public String welcomeDoctor(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Get the user's role from UserDetails (e.g., authorities)
        String userRole = userDetails.getAuthorities().isEmpty() ? "" : userDetails.getAuthorities().iterator().next().getAuthority();

        String username = userDetails.getUsername();
        User user = userService.findByEmail(username);

        // Assuming User has properties firstName and lastName
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        UserDto userInfo = new UserDto();
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);

        // Add the userRole and userFullName to the model
        model.addAttribute("userRole", userRole);
        model.addAttribute("userInfo", userInfo);

        return "welcome_doctor";
    }
}
