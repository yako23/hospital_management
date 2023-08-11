package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.AppointmentService;
import com.example.registrationlogindemo.service.DoctorService;
import com.example.registrationlogindemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserService userService;

    @Autowired
    public DoctorController(DoctorService doctorService, AppointmentService appointmentService, UserService userService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @GetMapping("/doctors")
    public String showDoctorSearchPage(Model model, @RequestParam(value = "specialty", required = false) String specialty) {
        List<Doctor> doctors;
        if (specialty != null && !specialty.isEmpty()) {
            doctors = doctorService.getDoctorsBySpecialty(specialty);
        } else {
            doctors = doctorService.getAllDoctors();
        }
        model.addAttribute("doctors", doctors);
        return "doctors"; // The name of the Thymeleaf template for the doctor search page
    }

    @GetMapping("/getDoctorsBySpecialty")
    @ResponseBody
    public List<Doctor> getDoctorsBySpecialty(@RequestParam String specialty) {
        List<Doctor> doctors = doctorService.getDoctorsBySpecialty(specialty);
        return doctors;
    }

    @GetMapping("/doctor/appointments")
    public String viewDoctorAppointments(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // Handle the case where the user is not authenticated
            // You can return an error view or redirect to a login page
            return "redirect:/login";
        }
        String email = userDetails.getUsername();
        Doctor doctor = doctorService.findByUsername(email);
        if (doctor == null) {
            // Handle the case where the doctor is not found
            // You can return an error view or redirect to an appropriate page
            // For example: model.addAttribute("errorMessage", "Doctor not found.");
            return "custom-403";
        }
        List<Object[]> appointmentDetails = doctorService.getAppointmentDetailsByDoctorId(doctor.getId());
        for (Object[] detail : appointmentDetails) {
            System.out.println("Appointment Detail: " + Arrays.toString(detail));
        }

        model.addAttribute("appointmentDetails", appointmentDetails);
        model.addAttribute("doctor", doctor);

        // Get the userId from the authenticated user and add it to the model
        User user = userService.findByEmail(email);
        model.addAttribute("userId", user.getId());
        return "doctor_appointments";
    }
}
