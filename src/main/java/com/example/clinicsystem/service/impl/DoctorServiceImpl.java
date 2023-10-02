package com.example.clinicsystem.service.impl;

import com.example.clinicsystem.entity.Doctor;
import com.example.clinicsystem.entity.User;
import com.example.clinicsystem.repository.DoctorRepository;
import com.example.clinicsystem.repository.UserRepository;
import com.example.clinicsystem.service.DoctorService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;


    public DoctorServiceImpl(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId).get();
    }

    @Override
    public List<Object[]> getAppointmentDetailsByDoctorId(Long doctorId) {
        return doctorRepository.findAppointmentDetailsByDoctorId(doctorId);
    }

    @Override
    public Doctor findByEmail(String email) {
        return doctorRepository.findByUser_Email(email);
    }

    @Override
    public Doctor findByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Δεν βρέθηκε χρήστης με username: " + email);
        }
        return doctorRepository.findByUser(user);
    }

}
