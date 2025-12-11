package com.university.cursus.repositories;
// ... imports
import com.university.cursus.models.Professor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByProfessorId(String professorId);
}