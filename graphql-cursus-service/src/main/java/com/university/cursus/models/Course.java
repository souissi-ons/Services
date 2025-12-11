package com.university.cursus.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code; 
    
    private String name;
    
    private String description;
    
    private String level;
    
    private String speciality;
    
    private Integer durationYears;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Module> modules = new ArrayList<>();
    
    private Integer totalCredits;
    
    private String coordinator;
    
    private boolean active;
}