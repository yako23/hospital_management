package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Diagnosis;
import com.example.registrationlogindemo.service.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DiagnosisController {

    private final AppointmentService appointmentService;

    public DiagnosisController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/diagnoses/save")
    public String saveDiagnosis(@ModelAttribute("diagnosis") Diagnosis diagnosis) {
        // ... Existing code ...

        // Get the appointment by its ID
        Appointment appointment = appointmentService.getAppointmentById(diagnosis.getAppointment().getId());

        if (appointment != null) {
            // Update the appointment's status
            appointment.setStatus("ΟΛΟΚΛΗΡΩΜΕΝΟ");
            appointmentService.saveAppointment(appointment);
        }

        return "doctor_appointments";
    }

}
