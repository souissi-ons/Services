package com.university.cursus.repositories;

import com.university.cursus.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findByLevel(String level);
    List<Course> findBySpeciality(String speciality);
    List<Course> findByActive(boolean active);
}