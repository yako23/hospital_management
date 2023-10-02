package com.example.clinicsystem.controller;

import com.example.clinicsystem.dto.AppointmentDto;
import com.example.clinicsystem.dto.DoctorDto;
import com.example.clinicsystem.entity.Appointment;
import com.example.clinicsystem.entity.Doctor;
import com.example.clinicsystem.entity.User;
import com.example.clinicsystem.service.AppointmentService;
import com.example.clinicsystem.service.DoctorService;
import com.example.clinicsystem.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/appointment")
    public String showAppointmentForm(Model model){
        model.addAttribute("appointment", new AppointmentDto());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByEmail(username);

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
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

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

        // Save the appointment
        appointmentService.saveAppointment(appointment);
        redirectAttributes.addFlashAttribute("successMessage", "Η κράτηση ολοκληρώθηκε επιτυχώς!");
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
}