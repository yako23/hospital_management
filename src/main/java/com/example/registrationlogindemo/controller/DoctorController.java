package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.AppointmentDto;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.service.AppointmentService;
import com.example.registrationlogindemo.service.DoctorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
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

    @GetMapping("/doctor/appointments")
    public String viewDoctorAppointments(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Doctor doctor = doctorService.findByUsername(username);
        List<AppointmentDto> appointments = appointmentService.getAppointmentsByDoctorId(doctor.getId());

        model.addAttribute("appointments", appointments);
        return "doctor_appointments";
    }
}
