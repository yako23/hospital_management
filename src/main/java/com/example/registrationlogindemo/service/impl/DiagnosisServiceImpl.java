package com.example.registrationlogindemo.service.impl;

import com.example.registrationlogindemo.entity.Diagnosis;
import com.example.registrationlogindemo.repository.DiagnosisRepository;
import com.example.registrationlogindemo.service.DiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiagnosisServiceImpl implements DiagnosisService {
    private final DiagnosisRepository diagnosisRepository;

    @Autowired
    public DiagnosisServiceImpl(DiagnosisRepository diagnosisRepository) {
        this.diagnosisRepository = diagnosisRepository;
    }

    @Override
    public Diagnosis saveDiagnosis(Diagnosis diagnosis) {
        return diagnosisRepository.save(diagnosis);
    }
    @Override
    public List<Diagnosis> getAllDiagnoses() {
        return diagnosisRepository.findAll();
    }
    @Override
    public Optional<Diagnosis> getDiagnosisById(Long id) {
        return diagnosisRepository.findById(id);
    }

    @Override
    public List<Diagnosis> getDiagnosesByDoctorId(Long doctorId) {
        return diagnosisRepository.findByDoctorId(doctorId);
    }

}
