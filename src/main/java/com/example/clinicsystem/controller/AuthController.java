package com.example.clinicsystem.controller;

import com.example.clinicsystem.dto.UserDto;
import com.example.clinicsystem.entity.Doctor;
import com.example.clinicsystem.entity.User;
import com.example.clinicsystem.repository.DoctorRepository;
import com.example.clinicsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    private UserService userService;
    private DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, DoctorRepository doctorRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
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

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        if (user != null) {
            // Create a UserDto and populate it with the user's current data
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setEmail(user.getEmail());
            userDto.setAmka(user.getAmka());
            userDto.setPhNo(user.getPhNo());

            model.addAttribute("user", userDto);
            return "update-user";
        } else {
            // Handle the case where the user is not found
            return "redirect:/custom-403"; // or any other appropriate action
        }
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("user") UserDto userDto) {
        // Retrieve the user's current data from the database using email
        String email = userDto.getEmail();
        User existingUser = userService.findByEmail(email);

        if (existingUser != null) {
            // Update the fields of the existing user object with data from UserDto
            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setAmka(userDto.getAmka());
            existingUser.setPhNo(userDto.getPhNo());

            // Encrypt and update the password (if needed)
            // For password encryption, use the PasswordEncoder bean
            if (!userDto.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(userDto.getPassword());
                existingUser.setPassword(encodedPassword);
            }

            // Save the updated user object
            userService.editUser(existingUser);

            // Redirect to the profile page or any other appropriate page
            return "redirect:/user-update";
        } else {
            // Handle the case where the user is not found
            return "redirect:/custom-403"; // or any other appropriate action
        }
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
