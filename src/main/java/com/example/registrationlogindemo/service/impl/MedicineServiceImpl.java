package com.example.registrationlogindemo.service.impl;

import com.example.registrationlogindemo.entity.Medicine;
import com.example.registrationlogindemo.repository.MedicineRepository;
import com.example.registrationlogindemo.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository medicineRepository;

    @Autowired
    public MedicineServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }
    @Override
    public Medicine saveMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }
    @Override
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }
    @Override
    public Optional<Medicine> getMedicineById(Long id) {
        return medicineRepository.findById(id);
    }
    @Override
    public void deleteMedicineById(Long id) {
        medicineRepository.deleteById(id);
    }
}
