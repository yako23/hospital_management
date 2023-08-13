package com.example.registrationlogindemo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "app_date")
    private Date app_date;

    @Column(name = "doc_specialty")
    private String doc_specialty;

    @Column(name = "reason")
    private String reason;

    @Column()
    private Long doctor_id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "status", columnDefinition = "varchar(20) default 'ΕΚΚΡΕΜΕΙ'")
    private String status;

    @ManyToOne
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToOne(mappedBy = "appointment")
    private Diagnosis diagnosis;

}