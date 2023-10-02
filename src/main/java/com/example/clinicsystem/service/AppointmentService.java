package com.example.clinicsystem.service;

import com.example.clinicsystem.dto.AppointmentDto;
import com.example.clinicsystem.entity.Appointment;
import com.example.clinicsystem.entity.Doctor;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();

    Appointment getAppointmentById(Long id);

    public List<AppointmentDto> getAppointmentsByDoctorId(Long doctorId);

    List<Appointment> getAppointmentsByPatientId(Long userId);


    void saveAppointment(Appointment appointment); // Add this method to save an Appointment entity

    Long findDoctorIdByName(String doctorFirstName, String doctorLastName);
    List<AppointmentDto> getAppointmentsByUserId(Long userId);


    List<Appointment> searchAppointments(String amka, String specialty);
    List<Object[]> getAppointmentsByDateAndSpecialty(Date searchDate, String specialty);

    Date parseDateString(String dateString) throws ParseException;

    List<Appointment> getAppointmentsByPatientEmail(String email);

    void deleteAppointment(Long id);
}

