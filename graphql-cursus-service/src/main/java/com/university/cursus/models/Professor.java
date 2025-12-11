package com.university.cursus.models;
import java.util.List;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String professorId; 
    private String firstName;
    private String lastName;
    private String email;
    private String department; 

    @Enumerated(EnumType.STRING)
    private ProfessorStatus status;

    @Transient 
    private List<Module> modulesTaught;
    
    public enum ProfessorStatus {
        FULL_TIME, PART_TIME, RESEARCHER, ADMINISTRATIVE
    }
}