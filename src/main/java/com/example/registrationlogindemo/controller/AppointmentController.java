package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.AppointmentDto;
import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.AppointmentService;
import com.example.registrationlogindemo.service.DoctorService;
import com.example.registrationlogindemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;
    private UserService userService;
    private DoctorService doctorService;

    public AppointmentController(AppointmentService appointmentService, UserService userService, DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.userService=userService;
        this.doctorService=doctorService;
    }


    // handler method to handle appointment booking request
    @GetMapping("/appointment")
    public String showAppointmentForm(Model model){
        model.addAttribute("appointment", new AppointmentDto());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByEmail(username);
        //Long userId = user.getId();

        model.addAttribute("userId", user.getId());
        // Retrieve the list of doctors from the database and set it in the model
        List<Doctor> doctors = doctorService.getAllDoctors(); // Assuming you have a method to get all doctors
        model.addAttribute("doctors", doctors);
        return "booking_form";
    }

    // handler method to handle appointment booking form submit request
 /*   @PostMapping("/appointment/save")
    public String booking(@Valid @ModelAttribute("appointment") AppointmentDto appointment,
                          @RequestParam("userId") Long userId,
                          BindingResult result,
                               Model model){

        if (result.hasErrors()) {
            // Handle validation errors
            // You might want to re-populate the list of doctors here as well
            *//*List<Doctor> doctors = doctorService.getAllDoctors();
            model.addAttribute("doctors", doctors);*//*
            return "booking_form";
        }

        //Doctor selectedDoctor = doctorService.getDoctorById(appointment.getDoctorId());
        // You can set the selected doctor in the appointmentDto, so it can be saved
        //appointment.setSelectedDoctor(selectedDoctor);

        // Convert AppointmentDto to Appointment entity
        //appointment.setUserId(appointment.getUserId());

        // Fetch the selected doctor
        Doctor selectedDoctor = doctorService.getDoctorById(appointment.getDoctorId());

        // Set the user ID from the hidden input field
        appointment.setUserId(userId);
        // Set the selected doctor in the appointmentDto
        appointment.setSelectedDoctor(selectedDoctor);
        appointment.setAppDate(appointment.getAppDate());
        appointment.setDocSpecialty(appointment.getDocSpecialty());
        appointment.setReason(appointment.getReason());
        //appointment.setSelectedDoctor(appointment.getSelectedDoctor());
        appointment.setOutput(appointment.getOutput());
        appointment.setId(appointment.getId());
        // Set the selected doctor in the appointmentDto
        appointment.setSelectedDoctor(selectedDoctor);
        Long doctorId = appointmentService.findDoctorIdByName(appointment.getSelectedDoctor());
        if (doctorId == null) {
            model.addAttribute("errorMessage", "Doctor not found.");
            return "booking_form";
        }
        // Set the retrieved doctorId in the appointmentDto
        appointment.setDoctorId(doctorId);
        // Try to save the appointment
        try {
            appointmentService.saveAppointment(appointment);
            model.addAttribute("successMessage", "Appointment booked successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error booking appointment. Please try again.");
        }
        return "redirect:/appointment/success";
}*/


}