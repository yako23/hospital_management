package com.example.registrationlogindemo.dto;

import com.example.registrationlogindemo.entity.Doctor;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

    private Integer id;
    private Date appDate;
    private String docSpecialty;
    @NotEmpty(message = "Ο Λόγος Εξέτασης δεν πρέπει να είναι κενός!")
    private String reason;
    private Long doctorId;
    private Long userId;
    private String output;
    private Doctor selectedDoctor; // Store the selected doctor entity


    }