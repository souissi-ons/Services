package com.university.cursus.controllers;

import com.university.cursus.models.Student;
import com.university.cursus.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
public class StudentController {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @QueryMapping
    public Student studentById(@Argument Long id) {
        return studentRepository.findById(id).orElse(null);
    }
    
    @QueryMapping
    public Student studentByStudentId(@Argument String studentId) {
        return studentRepository.findByStudentId(studentId).orElse(null);
    }
    
    @QueryMapping
    public List<Student> allStudents() {
        return studentRepository.findAll();
    }
    
    @QueryMapping
    public List<Student> studentsByLevel(@Argument String level) {
        return studentRepository.findByLevel(level);
    }

    @QueryMapping
    public List<Student> studentsBySpeciality(@Argument String speciality) {
        return studentRepository.findBySpeciality(speciality);
    }


    @MutationMapping
    public Student createStudent(@Argument StudentInput input) {
        if (studentRepository.findByStudentId(input.studentId()).isPresent()) {
            throw new RuntimeException("Un étudiant avec cet ID existe déjà : " + input.studentId());
        }

        Student student = new Student();
        student.setStudentId(input.studentId());
        student.setFirstName(input.firstName());
        student.setLastName(input.lastName());
        student.setEmail(input.email());
        if(input.birthDate() != null) student.setBirthDate(LocalDate.parse(input.birthDate()));
        student.setLevel(input.level());
        student.setSpeciality(input.speciality());
        student.setEnrollmentDate(LocalDate.now());
        student.setStatus(input.status() != null ? input.status() : Student.StudentStatus.ACTIVE);

        return studentRepository.save(student);
    }

    @MutationMapping
    public Student updateStudent(@Argument Long id, @Argument StudentInput input) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Étudiant introuvable ID: " + id));

        if (input.firstName() != null) student.setFirstName(input.firstName());
        if (input.lastName() != null) student.setLastName(input.lastName());
        if (input.email() != null) student.setEmail(input.email());
        if (input.level() != null) student.setLevel(input.level());
        if (input.speciality() != null) student.setSpeciality(input.speciality());
        if (input.status() != null) student.setStatus(input.status());
        if (input.birthDate() != null) student.setBirthDate(LocalDate.parse(input.birthDate()));

        return studentRepository.save(student);
    }
    
    @MutationMapping
    public Boolean deleteStudent(@Argument Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public record StudentInput(
        String studentId,
        String firstName,
        String lastName,
        String email,
        String birthDate,
        String level,
        String speciality,
        Student.StudentStatus status
    ) {}
}