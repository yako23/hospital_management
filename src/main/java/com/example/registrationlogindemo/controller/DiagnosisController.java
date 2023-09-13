package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Diagnosis;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.UserRepository;
import com.example.registrationlogindemo.service.AppointmentService;
import com.example.registrationlogindemo.service.DiagnosisService;
import com.example.registrationlogindemo.service.DoctorService;
import com.example.registrationlogindemo.service.MedicineService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DiagnosisController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final DiagnosisService diagnosisService;
    private final MedicineService medicineService;
    private final UserRepository userRepository;

    public DiagnosisController(AppointmentService appointmentService, DoctorService doctorService, DiagnosisService diagnosisService, MedicineService medicineService, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.diagnosisService = diagnosisService;
        this.medicineService = medicineService;
        this.userRepository = userRepository;
    }

    @PostMapping("/diagnoses/save")
    public String saveDiagnosis(@ModelAttribute("diagnosis") Diagnosis diagnosis,
                                @RequestParam("appointment.id") Long appointmentId,
                                @AuthenticationPrincipal UserDetails userDetails) {
        // ... Existing code ...

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

            // Get the logged-in doctor's ID
            String email = userDetails.getUsername();
            User doctor = doctorService.findByUsername(email).getUser();
            diagnosis.setDoctor(doctor);

            // Set the patient for the diagnosis
            User patient = appointment.getUser();
            diagnosis.setPatient(patient);

            // Save the diagnosis
            diagnosisService.saveDiagnosis(diagnosis);
        }

        return "redirect:/doctor/appointments";
    }

    @GetMapping("/diagnoses/by-doctor")
    public String showDoctorDiagnoses(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Retrieve the doctor's ID from the logged-in user
        String email = userDetails.getUsername();
        Doctor doctor = doctorService.findByUsername(email);

        if (doctor == null) {
            // Handle the case where the doctor is not found
            return "custom-403";
        }

        // Add the Doctor's ID to the model
        model.addAttribute("doctorId", doctor.getId());

        List<Diagnosis> diagnoses = diagnosisService.getDiagnosesByDoctorId(doctor.getId());
        model.addAttribute("diagnoses", diagnoses);

        return "doctor_diagnoses";
    }

    @GetMapping("/diagnoses/by-appointment/{appointmentId}")
    public String diagnoseAppointment(@PathVariable Long appointmentId,
                                      Model model,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Doctor doctor = doctorService.findByUsername(email);
        if (doctor == null) {
            return "custom-403";
        }

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        /*if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            // Handle the case where the appointment is not found or doesn't belong to the doctor
            return "custom-403";
        }*/

        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("appointment", appointment);
        return "diagnose_appointment";
    }



    /*@PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("diagnosisId") Long diagnosisId,
                             RedirectAttributes redirectAttributes) {

        // Handle file upload and storage here, generate a file URL
        try {
            String storagePath = "C:\\Users\\Yannis\\Desktop";
            Path filePath = Paths.get(storagePath + uniqueFilename);
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            // Handle the exception, e.g., log it or return an error message
        }

        // Update the Diagnosis object with the file URL
        Diagnosis diagnosis = diagnosisService.getDiagnosisById(diagnosisId);
        diagnosis.setFileUrl(fileUrl);

        // Save the updated Diagnosis object
        diagnosisService.saveDiagnosis(diagnosis);

        redirectAttributes.addFlashAttribute("message", "File uploaded successfully.");
        return "redirect:/diagnoses";
    }*/
}
