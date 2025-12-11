package com.university.cursus.repositories;

import com.university.cursus.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByModuleId(Long moduleId);
    List<Enrollment> findByAcademicYear(String academicYear);
    List<Enrollment> findByStatus(Enrollment.EnrollmentStatus status);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.academicYear = :year")
    List<Enrollment> findByStudentIdAndAcademicYear(@Param("studentId") Long studentId, 
                                                     @Param("year") String year);
}