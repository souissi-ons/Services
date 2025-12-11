package com.university.cursus.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String studentId; // ETU001, ETU002
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private LocalDate birthDate;
    
    private String level; // LICENCE_1, LICENCE_2, LICENCE_3, MASTER_1, MASTER_2
    
    private String speciality; // GINF, GIND, GC, etc.
    
    private LocalDate enrollmentDate;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();
    
    private Double gpa;
    
    private Integer totalCredits;
    
    @Enumerated(EnumType.STRING)
    private StudentStatus status; // ACTIVE, SUSPENDED, GRADUATED, DROPPED
    
    public enum StudentStatus {
        ACTIVE, SUSPENDED, GRADUATED, DROPPED
    }
}