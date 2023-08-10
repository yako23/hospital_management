package com.example.registrationlogindemo.repository;

import com.example.registrationlogindemo.entity.Appointment;
import com.example.registrationlogindemo.entity.Doctor;
import com.example.registrationlogindemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findBySpecialty(String specialty);

    Doctor findByUserFirstNameAndUserLastName(String docFirstName,String docLastName);

    // Define a query method to find appointments by doctorId
    @Query("SELECT a.app_date, u.firstName, u.lastName, a.reason, a.status " +
            "FROM Doctor d " +
            "JOIN d.appointments a " +
            "JOIN a.user u " +
            "WHERE d.id = :doctorId")
    List<Object[]> findAppointmentDetailsByDoctorId(/*@Param("doctor_id")*/ Long doctorId);

    Doctor findByUser(User user);

    Doctor findByUser_Email(String email);

}


