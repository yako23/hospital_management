package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Diagnosis;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.service.AppointmentService;
import com.example.registrationlogindemo.service.DiagnosisService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final DiagnosisService diagnosisService;

    public UserController(AppointmentService appointmentService,
                          UserRepository userRepository, DiagnosisService diagnosisService) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.diagnosisService = diagnosisService;
    }

    @GetMapping("/mybookings")
    public String viewPatientAppointments(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the user is a patient
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Get the authenticated user's username (email)
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email);

        // Retrieve the patient's appointments based on their username
        List<Appointment> patientAppointments = appointmentService.getAppointmentsByPatientEmail(email);

        // Add the patient's appointments to the model
        model.addAttribute("appointments", patientAppointments);
        model.addAttribute("user", user);


        return "mybookings";
    }

    @GetMapping("/delete-appointment/{id}")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Check if the appointment exists
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            // Handle the case where the appointment doesn't exist
            // You can display an error message or redirect to a relevant page
            return "redirect:/mybookings"; // Redirect back to the appointments page
        }

        // Check if the appointment's status is "ΕΚΚΡΕΜΕΙ"
        if ("ΕΚΚΡΕΜΕΙ".equals(appointment.getStatus())) {
            // Delete the appointment
            appointmentService.deleteAppointment(id);
            // Add a flash attribute for the success message
            redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled successfully.");

        }

        // Redirect back to the appointments page
        return "redirect:/mybookings";
    }

    @GetMapping("/diagnoses/by-patient")
    public String viewPatientDiagnoses(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the user is a patient
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Get the authenticated user's username (email)
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email);

        // Retrieve the patient's diagnoses based on their username (email)
        List<Diagnosis> patientDiagnoses = diagnosisService.getDiagnosesByPatientEmail(email);

        model.addAttribute("user", user);
        // Add the patient's diagnoses to the model
        model.addAttribute("diagnoses", patientDiagnoses);
        // You can add the patient's ID or other relevant information here if needed

        return "patient_diagnoses";
    }

    @GetMapping("/user_home")
    public String calculator(Model model) {
        return "user_home";
    }
}
