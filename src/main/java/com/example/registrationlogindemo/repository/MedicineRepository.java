package com.example.registrationlogindemo.repository;

import com.example.registrationlogindemo.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

}
