package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.entity.Medicine;

import java.util.List;
import java.util.Optional;

public interface MedicineService {

    Medicine saveMedicine(Medicine medicine);

    List<Medicine> getAllMedicines();

    Optional<Medicine> getMedicineById(Long id);

    void deleteMedicineById(Long id);
}


