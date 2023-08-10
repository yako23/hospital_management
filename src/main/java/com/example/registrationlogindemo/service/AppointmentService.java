package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.dto.AppointmentDto;
import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Doctor;

import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();

    Appointment getAppointmentById(Long id);

    public List<AppointmentDto> getAppointmentsByDoctorId(Long doctorId);

    List<Appointment> getAppointmentsByPatientId(Long userId);

    Appointment createAppointment(Appointment appointment, Long doctorId, Long userId);

    void saveAppointment(AppointmentDto appointmentDto);

    void saveAppointment(Appointment appointment); // Add this method to save an Appointment entity

    Long findDoctorIdByName(String doctorFirstName, String doctorLastName);
    List<AppointmentDto> getAppointmentsByUserId(Long userId);

    Long findDoctorIdByName(Doctor selectedDoctor);

    List<AppointmentDto> getAppointmentsBySpecialty(String specialty);
}

