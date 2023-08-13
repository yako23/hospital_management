package com.example.registrationlogindemo.repository;

import com.example.registrationlogindemo.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findByDoctorId(Long doctorId);
}
