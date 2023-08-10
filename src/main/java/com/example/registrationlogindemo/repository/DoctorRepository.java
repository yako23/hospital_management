package com.example.registrationlogindemo.repository;

import com.example.registrationlogindemo.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findBySpecialty(String specialty);

    Doctor findByUserFirstNameAndUserLastName(String docFirstName,String docLastName);
}


