package com.example.clinicsystem.controller;

import com.example.clinicsystem.entity.Appointment;
import com.example.clinicsystem.entity.Diagnosis;
import com.example.clinicsystem.entity.Doctor;
import com.example.clinicsystem.entity.User;
import com.example.clinicsystem.repository.UserRepository;
import com.example.clinicsystem.service.AppointmentService;
import com.example.clinicsystem.service.DiagnosisService;
import com.example.clinicsystem.service.DoctorService;
import com.example.clinicsystem.service.MedicineService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
                                @RequestParam("image") MultipartFile multipartFile,
                                @RequestParam("appointment.id") Long appointmentId,
                                @AuthenticationPrincipal UserDetails userDetails) throws IOException{

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

            if (!multipartFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                diagnosis.setFileUrl(fileName);

                Diagnosis savedDiagnosis = diagnosisService.saveDiagnosis(diagnosis);

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
                // Save the diagnosis
                diagnosisService.saveDiagnosis(diagnosis);
            }
        }
        return "redirect:/doctor/appointments";
    }

    @GetMapping("/diagnoses/by-doctor")
    public String showDoctorDiagnoses(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Retrieve the doctor's ID from the logged-in user
        String email = userDetails.getUsername();
        Doctor doctor = doctorService.findByUsername(email);

        if (doctor == null) {
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

        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("appointment", appointment);
        return "diagnose_appointment";
    }

}
