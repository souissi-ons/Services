package com.university.cursus.controllers;

import com.university.cursus.models.*;
import com.university.cursus.models.Module;
import com.university.cursus.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class EnrollmentController {
    
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ModuleRepository moduleRepository;
    
    @QueryMapping
    public Enrollment enrollmentById(@Argument Long id) {
        return enrollmentRepository.findById(id).orElse(null);
    }
    
    @QueryMapping
    public List<Enrollment> enrollmentsByStudent(@Argument Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
    
    @QueryMapping
    public List<Enrollment> enrollmentsByModule(@Argument Long moduleId) {
        return enrollmentRepository.findByModuleId(moduleId);
    }

    @QueryMapping
    public List<Enrollment> enrollmentsByYear(@Argument String academicYear) {
        return enrollmentRepository.findByAcademicYear(academicYear);
    }
    

    @MutationMapping
    @Transactional
    public Enrollment enrollStudent(@Argument Long studentId, @Argument Long moduleId, @Argument String academicYear) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Étudiant introuvable ID: " + studentId));
        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new RuntimeException("Module introuvable ID: " + moduleId));

        boolean exists = enrollmentRepository.findByStudentIdAndAcademicYear(studentId, academicYear)
                .stream()
                .anyMatch(e -> e.getModule().getId().equals(moduleId));

        if (exists) {
            throw new RuntimeException("ERREUR : L'étudiant " + student.getFirstName() + 
                                     " est DÉJÀ inscrit au module " + module.getName() + 
                                     " pour l'année " + academicYear);
        }

        if (student.getStatus() != Student.StudentStatus.ACTIVE) {
            throw new RuntimeException("Impossible d'inscrire un étudiant avec le statut: " + student.getStatus());
        }

        if (module.getMaxStudents() != null && module.getMaxStudents() > 0) {
            long currentCount = enrollmentRepository.findByModuleId(moduleId).stream()
                    .filter(e -> e.getAcademicYear().equals(academicYear))
                    .count();
            if (currentCount >= module.getMaxStudents()) {
                throw new RuntimeException("Capacité du module atteinte (" + module.getMaxStudents() + ")");
            }
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setModule(module);
        enrollment.setAcademicYear(academicYear);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
        
        return enrollmentRepository.save(enrollment);
    }
    @MutationMapping
    public Enrollment updateEnrollmentStatus(@Argument Long id, @Argument Enrollment.EnrollmentStatus status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inscription introuvable ID: " + id));
        enrollment.setStatus(status);
        return enrollmentRepository.save(enrollment);
    }

    @MutationMapping
    @Transactional
    public Enrollment addGrade(@Argument Long enrollmentId, @Argument GradeInput gradeInput) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Inscription introuvable ID: " + enrollmentId));
        
        Grade grade = new Grade();
        grade.setEnrollment(enrollment);
        grade.setType(gradeInput.type());
        grade.setScore(gradeInput.score());
        grade.setCoefficient(gradeInput.coefficient());
        grade.setExamDate(gradeInput.examDate() != null ? LocalDate.parse(gradeInput.examDate()) : null);
        grade.setExaminer(gradeInput.examiner());
        grade.setComments(gradeInput.comments());
        
        enrollment.getGrades().add(grade);
        
        double totalWeighted = enrollment.getGrades().stream()
            .mapToDouble(g -> g.getScore() * g.getCoefficient())
            .sum();
        double totalCoeff = enrollment.getGrades().stream()
            .mapToDouble(Grade::getCoefficient)
            .sum();
            
        if (totalCoeff > 0) {
            enrollment.setFinalGrade(totalWeighted / totalCoeff);
        }
        
        return enrollmentRepository.save(enrollment);
    }
    
    @SchemaMapping(typeName = "Enrollment", field = "student")
    public Student student(Enrollment enrollment) { return enrollment.getStudent(); }
    
    @SchemaMapping(typeName = "Enrollment", field = "module")
    public Module module(Enrollment enrollment) { return enrollment.getModule(); }
    
    @SchemaMapping(typeName = "Enrollment", field = "grades")
    public List<Grade> grades(Enrollment enrollment) { return enrollment.getGrades(); }
    
    record GradeInput(
        Grade.GradeType type,
        Double score,
        Double coefficient,
        String examDate,
        String examiner,
        String comments
    ) {}
}