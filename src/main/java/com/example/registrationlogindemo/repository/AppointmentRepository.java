package com.example.registrationlogindemo.repository;

import com.example.registrationlogindemo.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    Appointment findById(Long id);
    //List<Appointment> findBySelectedDoctorName(String email);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByUserId(Long userId);
    List<Appointment> findByDoctor_User_FirstNameAndDoctor_User_LastName(String firstName, String lastName);

    Optional<Appointment> findByDiagnosisId(Long diagnosisId);
}
