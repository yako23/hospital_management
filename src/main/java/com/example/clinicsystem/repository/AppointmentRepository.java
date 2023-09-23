package com.example.clinicsystem.repository;

import com.example.clinicsystem.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
    List<Appointment> findByUserAmkaAndDoctorSpecialty(String amka, String specialty);

    @Query("SELECT a.app_date, u.firstName, u.lastName, a.reason, a.status, a.id " +
            "FROM Appointment a " +
            "JOIN a.user u " +
            "WHERE a.app_date = :searchDate " +
            "AND u.doc_specialty = :specialty")
    List<Object[]> findAppointmentsByDateAndSpecialty(@Param("searchDate") Date searchDate,
                                                      @Param("specialty") String specialty);

    List<Appointment> findByUserEmail(String email);

    void deleteById(Long id);
}
