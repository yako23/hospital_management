package com.example.clinicsystem.repository;

import com.example.clinicsystem.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findByDoctorId(Long doctorId);

    List<Diagnosis> findByPatientEmail(String email);

    List<Diagnosis> findByPatientId(Long patientId);
}
