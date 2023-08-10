package com.example.registrationlogindemo.service.impl;

import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.repository.DoctorRepository;
import com.example.registrationlogindemo.service.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;


    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }



    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId).get();
    }

    @Override
    public Doctor findByUsername(String username) {
        return null;
    }
}
