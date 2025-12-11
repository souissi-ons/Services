package com.university.cursus.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enrollment", uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_module_year", columnNames = {"student_id", "module_id", "academicYear"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;
    
    private String academicYear;

    private LocalDateTime enrollmentDate;
    
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Grade> grades = new ArrayList<>();
    
    private Double finalGrade;
    
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;
    
    private Integer attendance;

    public enum EnrollmentStatus {
        ENROLLED, COMPLETED, FAILED, WITHDRAWN
    }
}