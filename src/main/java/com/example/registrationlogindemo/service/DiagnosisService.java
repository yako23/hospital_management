package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.entity.Diagnosis;

import java.util.List;
import java.util.Optional;

public interface DiagnosisService {

    Diagnosis saveDiagnosis(Diagnosis diagnosis);

    List<Diagnosis> getAllDiagnoses();

    Optional<Diagnosis> getDiagnosisById(Long id);

    List<Diagnosis> getDiagnosesByDoctorId(Long doctorId);

    List<Diagnosis> getDiagnosesByPatientEmail(String email);
    List<Diagnosis> getDiagnosesByPatientId(Long patientId);
}
