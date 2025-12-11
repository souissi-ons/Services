package com.university.cursus.repositories;

import com.university.cursus.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    List<Student> findByLevel(String level);
    List<Student> findBySpeciality(String speciality);
    List<Student> findByStatus(Student.StudentStatus status);
}