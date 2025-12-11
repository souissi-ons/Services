package com.university.cursus.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;
    
    @Enumerated(EnumType.STRING)
    private GradeType type; 
    
    private Double score;
    
    private Double coefficient;
    
    private LocalDate examDate;
    
    private String examiner;
    
    private String comments;
    
    public enum GradeType {
        CC, DS, EXAMEN, PROJET, TP, ORAL
    }
}