package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.AppointmentDto;
import com.example.registrationlogindemo.dto.DoctorDto;
import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.AppointmentService;
import com.example.registrationlogindemo.service.DoctorService;
import com.example.registrationlogindemo.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;
    private UserService userService;
    private DoctorService doctorService;
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);


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

    @PostMapping("/appointment/save")
    public String saveAppointment(@ModelAttribute("appointment") AppointmentDto appointmentDto,
                                  @RequestParam("userId") Long userId,
                                  @RequestParam("doctorId") Long doctorId,
                                  Model model) {

        logger.info("Received appointment data: {}", appointmentDto);
        logger.info("Received userId: {}", userId);
        logger.info("Received doctorId: {}", doctorId);

        // Use userId to fetch the User entity (patient)
        User patient = userService.getUserById(userId);

        Doctor doctor = doctorService.getDoctorById(doctorId);

        // Create an Appointment entity from the DTO
        Appointment appointment = new Appointment();
        appointment.setDoc_specialty(appointmentDto.getDocSpecialty());
        appointment.setApp_date(appointmentDto.getAppDate());
        appointment.setReason(appointmentDto.getReason());
        appointment.setDoctor_id(doctorId);
        appointment.setUserId(userId);
        appointment.setStatus("ΕΚΚΡΕΜΕΙ");
        //appointment.setUser(patient);
        //.setDoctor(doctor);

        // Save the appointment
        appointmentService.saveAppointment(appointment);

        // Redirect to a success page or return a success message
        model.addAttribute("success", true);
        return "redirect:/appointment";
    }

    @GetMapping("/appointments/getDoctorsBySpecialty")
    @ResponseBody
    public List<DoctorDto> getDoctorsBySpecialty(@RequestParam String specialty) {
        List<Doctor> doctors = doctorService.getDoctorsBySpecialty(specialty);

        List<DoctorDto> doctorDTOs = doctors.stream().map(doctor -> {
            DoctorDto dto = new DoctorDto();
            dto.setId(doctor.getId());
            dto.setSpecialty(doctor.getSpecialty());
            dto.setFirstName(doctor.getUser().getFirstName());
            dto.setLastName(doctor.getUser().getLastName());
            return dto;
        }).collect(Collectors.toList());

        return doctorDTOs;
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