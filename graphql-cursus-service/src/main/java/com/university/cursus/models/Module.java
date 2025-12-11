package com.university.cursus.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Module {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code; 
    
    private String name;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    
    private String semester;
    
    private Integer credits;
    
    private Integer hours;
    
    @Enumerated(EnumType.STRING)
    private ModuleType type; 
    
    private String professor;
    
    private String prerequisite;
    
    private Integer maxStudents;
    
    private boolean mandatory;
    
    public enum ModuleType {
        COURS, TD, TP, PROJET, STAGE
    }
}