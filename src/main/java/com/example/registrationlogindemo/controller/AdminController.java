package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Diagnosis;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.AppointmentRepository;
import com.example.registrationlogindemo.repository.DoctorRepository;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.service.*;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class AdminController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final DiagnosisService diagnosisService;
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final MedicineService medicineService;

    public AdminController(AppointmentService appointmentService, UserRepository userRepository, DiagnosisService diagnosisService, DoctorRepository doctorRepository, DoctorService doctorService, AppointmentRepository appointmentRepository, UserService userService, MedicineService medicineService) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.diagnosisService = diagnosisService;
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
        this.medicineService = medicineService;
    }

    @GetMapping("/admin_appointments")
    public String viewAppointmentsForAdmin(Model model) {
        // Replace this with your logic to fetch appointment details for all doctors
        List<Appointment> appointmentDetails = appointmentService.getAllAppointments();

        // Add the appointment details to the model
        model.addAttribute("appointmentDetails", appointmentDetails);

        return "admin_appointments";
    }

    @PostMapping("/changeUserStatus")
    @ResponseBody
    public Map<String, Object> changeUserStatus(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Attempt to find the user by email
            User user = userService.findByEmail(email);

            if (user != null) {
                // User found
                // Update the user's status to "ΕΝΕΡΓΟΣ" or toggle it if it's a boolean
                String newStatus = user.getStatus().equals("ΕΚΚΡΕΜΕΙ") ? "ΕΝΕΡΓΟΣ" : "ΕΚΚΡΕΜΕΙ";
                user.setStatus(newStatus);
               // user.setStatus("ΕΝΕΡΓΟΣ");

                // Save the updated user
                userService.saveUser(user);

                response.put("success", true);
            } else {
                // User not found
                response.put("success", false);
                response.put("message", "User not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error changing user status");
        }

        return response;
    }


   /* @PostMapping("/changeUserStatus")
    @ResponseBody
    public Map<String, Object> changeUserStatus(@RequestParam("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Attempt to find the user by ID
            Optional<User> userOptional = userService.findById(userId);

            if (userOptional.isPresent()) {
                // User found
                User user = userOptional.get();

                // Update the user's status to "ΕΝΕΡΓΟΣ" or toggle it if it's a boolean
                user.setStatus("ΕΝΕΡΓΟΣ");

                // Save the updated user
                userService.saveUser(user);

                response.put("success", true);
            } else {
                // User not found
                response.put("success", false);
                response.put("message", "User not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error changing user status");
        }

        return response;
    }*/



    /*@PostMapping("/admin/change-status/{userId}")
    public ResponseEntity<String> changeStatus(@PathVariable Long userId) {
        try {

            // Retrieve the pending user by ID
            Optional<User> optionalUser = userService.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                // Check the current status and update it accordingly
               *//* if ("ΕΚΚΡΕΜΕΙ".equals(user.getStatus())) {*//*
                    user.setStatus("ΕΝΕΡΓΟΣ");
                    userService.saveUser(user); // Save the updated user

                    return ResponseEntity.ok("Status changed successfully");

                }

            return ResponseEntity.badRequest().body("Invalid user or status cannot be changed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error changing status");
        }
    }*/

    @GetMapping("/pending-users")
    public String showpendingUsers(Model model) {

        // Query the database to retrieve pending users
        List<User> pendingUsers = userService.getPendingUsers();

        // Add the list of pending users to the model
        model.addAttribute("pendingUsers", pendingUsers);

        /*List<PendingUser> pendingUsers = pendingUserService.getAllPendingUsers();
        model.addAttribute("pendingUsers", pendingUsers);*/
        return "pending_users";
    }

    @GetMapping("/admin/diagnoses")
    public String viewAllDiagnoses(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the user has the "ADMIN" authority
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            // Handle the case where the user is not an administrator
            return "custom-403";
        }

        // Retrieve all diagnoses
        List<Diagnosis> allDiagnoses = diagnosisService.getAllDiagnoses();
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        // Add the diagnoses to the model
        model.addAttribute("diagnoses", allDiagnoses);

        return "admin_diagnoses";
    }

    @GetMapping("/create")
    public String showDiagnosisForm(Model model) {
        // Fetch the list of available doctors
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        // Add other necessary attributes to the model

        return "admin_diagnose_app";
    }

    @GetMapping("/admin/diagnoses/by-appointment/{appointmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDiagnoseAppointment(@PathVariable Long appointmentId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Doctor doctor = doctorService.findByUsername(email);

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("appointment", appointment);
        return "admin_diagnose_app";
    }
    @PostMapping("/admin/diagnoses/save")
    public String saveAdminDiagnosis(@ModelAttribute("diagnosis") Diagnosis diagnosis,
                                     @RequestParam("appointment.id") Long appointmentId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model, @RequestParam("image") MultipartFile multipartFile) throws IOException {

        // Get the appointment by its ID
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        if (appointment != null) {
            // Update the appointment's status
            appointment.setStatus("ΟΛΟΚΛΗΡΩΜΕΝΟ");
            appointmentService.saveAppointment(appointment);

            // Set the appointment's date to the diagnosis
            diagnosis.setDate(appointment.getApp_date());

            // Set the appointment for the diagnosis
            diagnosis.setAppointment(appointment);

            // Set the doctor for the diagnosis
            diagnosis.setDoctor(appointment.getDoctor().getUser());

            // Set the patient for the diagnosis
            User patient = appointment.getUser();
            diagnosis.setPatient(patient);


            if (!multipartFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                diagnosis.setFileUrl(fileName);

                String uploadDir = "./diagnosis-photos/" + patient.getId();
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = multipartFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IOException("Could not save the uploaded file: " + fileName);
                }
            } else {
                // No file provided, set FileUrl to null
                diagnosis.setFileUrl(null);
            }

            // Save the diagnosis
            diagnosisService.saveDiagnosis(diagnosis);
        }

        return "redirect:/admin_appointments";
    }



}
