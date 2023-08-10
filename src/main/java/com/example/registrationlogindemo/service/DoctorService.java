package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Doctor;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DoctorService {
    List<Doctor> getDoctorsBySpecialty(@RequestParam("specialty") String specialty);

    List<Doctor> getAllDoctors();

    Doctor getDoctorById(Long doctorId);

    Doctor findByUsername(String email);

    List<Object[]> getAppointmentDetailsByDoctorId(Long id);

    Doctor findByEmail(String email);
}
