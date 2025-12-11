package com.university.auth.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; 

    @Column(nullable = false)
    private String password; 

    private String email;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        STUDENT, PROFESSOR, ADMIN
    }
}