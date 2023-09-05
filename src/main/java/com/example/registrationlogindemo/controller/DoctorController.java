package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.AppointmentRepository;
import com.example.registrationlogindemo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.List;

@Controller
//@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final DiagnosisService diagnosisService;
    private final MedicineService medicineService;

    @Autowired
    public DoctorController(DoctorService doctorService, AppointmentService appointmentService, AppointmentRepository appointmentRepository, UserService userService, DiagnosisService diagnosisService, MedicineService medicineService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
        this.diagnosisService = diagnosisService;
        this.medicineService = medicineService;
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

    @GetMapping("/doctor/search-history")
    public String searchAppointments(@RequestParam(name = "amka", required = false) String amka,
                                     Model model,
                                     @AuthenticationPrincipal UserDetails userDetails) {


        // Check if the user is a doctor
        if (userDetails == null) {
            return "redirect:/login";
        }

        String email = userDetails.getUsername();
        Doctor doctor = doctorService.findByUsername(email);

        if (doctor == null) {
            return "custom-403";
        }

        // Get the specialty of the doctor
        String specialty = doctor.getSpecialty();

        // If amka is provided, perform the search
        if (amka != null && !amka.isEmpty()) {
            List<Appointment> appointments = appointmentService.searchAppointments(amka, specialty);
            model.addAttribute("appointments", appointments);
        }

        model.addAttribute("doctorSpecialty", specialty);

        // Retrieve the appointments based on specialty and AMKA
        /*List<Appointment> appointments = appointmentService.searchAppointments(amka, specialty);

        // Pass the search results to the template
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctor", doctor);*/

        return "search_patient_history";
    }

    @GetMapping("/doctor/appointments/search")
    public String searchAppointmentsByDate(@RequestParam("searchDate") String searchDateStr,
                                           Model model,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the user is a doctor
        if (userDetails == null) {
            return "redirect:/login";
        }

        String email = userDetails.getUsername();
        Doctor doctor = doctorService.findByUsername(email);

        if (doctor == null) {
            return "custom-403";
        }

        // Get the specialty of the doctor
        String specialty = doctor.getSpecialty();

        // Convert the searchDateStr to a java.util.Date
        Date searchDate = null;
        try {
            searchDate = new SimpleDateFormat("yyyy-MM-dd").parse(searchDateStr);
        } catch (ParseException e) {
            // Handle the parsing error, e.g., show an error message to the user
        }

        // Retrieve the appointments for the entered date and specialty
        List<Object[]> appointmentDetails = appointmentService.getAppointmentsByDateAndSpecialty(searchDate, specialty);

        model.addAttribute("appointmentDetails", appointmentDetails);
        model.addAttribute("doctor", doctor);
        model.addAttribute("userId", doctor.getUser().getId());

        return "doctor_appointments";
    }

}
