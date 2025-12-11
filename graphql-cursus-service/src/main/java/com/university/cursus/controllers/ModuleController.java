package com.university.cursus.controllers;

import com.university.cursus.models.Course;
import com.university.cursus.models.Enrollment;
import com.university.cursus.models.Module;
import com.university.cursus.models.Professor;
import com.university.cursus.models.Student;
import com.university.cursus.repositories.CourseRepository;
import com.university.cursus.repositories.EnrollmentRepository;
import com.university.cursus.repositories.ModuleRepository;
import com.university.cursus.repositories.ProfessorRepository;
import com.university.cursus.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ModuleController {

    @Autowired private ModuleRepository moduleRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;


    @QueryMapping
    public Module moduleById(@Argument Long id) {
        return moduleRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public Module moduleByCode(@Argument String code) {
        return moduleRepository.findByCode(code).orElse(null);
    }

    @QueryMapping
    public List<Module> allModules() {
        return moduleRepository.findAll();
    }

    @QueryMapping
    public List<Module> modulesByCourse(@Argument Long courseId) {
        return moduleRepository.findByCourseId(courseId);
    }

    @QueryMapping
    public List<Module> modulesBySemester(@Argument String semester) {
        return moduleRepository.findBySemester(semester);
    }

    @QueryMapping
    public List<Module> modulesByProfessor(@Argument String professor) {
        return moduleRepository.findByProfessor(professor);
    }


    @MutationMapping
    @Transactional
    public Module createModule(@Argument ModuleInput input) {
        Course course = courseRepository.findById(Long.parseLong(input.courseId()))
            .orElseThrow(() -> new RuntimeException("Course introuvable ID: " + input.courseId()));
        
        if(moduleRepository.findByCode(input.code()).isPresent()){
            throw new RuntimeException("Code module déjà existant: " + input.code());
        }

        Module module = new Module();
        module.setCourse(course);
        updateModuleFromInput(module, input);
        
        return moduleRepository.save(module);
    }

    @MutationMapping
    @Transactional
    public Module updateModule(@Argument Long id, @Argument ModuleInput input) {
        Module module = moduleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Module introuvable ID: " + id));

        if (input.courseId() != null) {
            Course course = courseRepository.findById(Long.parseLong(input.courseId()))
                .orElseThrow(() -> new RuntimeException("Course introuvable ID: " + input.courseId()));
            module.setCourse(course);
        }

        updateModuleFromInput(module, input);
        return moduleRepository.save(module);
    }

    @MutationMapping
    public Boolean deleteModule(@Argument Long id) {
        if(moduleRepository.existsById(id)) {
            moduleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @MutationMapping
    @Transactional
    public Module affectProfessorToModule(@Argument String moduleCode, @Argument String professorId) {
        Module module = moduleRepository.findByCode(moduleCode)
            .orElseThrow(() -> new RuntimeException("Module introuvable code: " + moduleCode));
        
        Professor professor = professorRepository.findByProfessorId(professorId)
            .orElseThrow(() -> new RuntimeException("Professeur introuvable ID: " + professorId));


        module.setProfessor(professor.getProfessorId()); 
        
        return moduleRepository.save(module);
    }

    @MutationMapping
    @Transactional
    public Module registerStudentToCourse(@Argument String moduleCode, @Argument String studentId) {
        Module module = moduleRepository.findByCode(moduleCode)
             .orElseThrow(() -> new RuntimeException("Module introuvable: " + moduleCode));
        
        Student student = studentRepository.findByStudentId(studentId)
             .orElseThrow(() -> new RuntimeException("Étudiant introuvable: " + studentId));

        boolean exists = enrollmentRepository.findByStudentIdAndAcademicYear(student.getId(), "CURRENT") 
                .stream().anyMatch(e -> e.getModule().getId().equals(module.getId()));
        
        if (exists) {
            throw new RuntimeException("Étudiant déjà inscrit à ce module.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setModule(module);
        enrollment.setAcademicYear("2024-2025"); 
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
        
        enrollmentRepository.save(enrollment);
        
        return module; 
    }


    private void updateModuleFromInput(Module module, ModuleInput input) {
        if(input.code() != null) module.setCode(input.code());
        if(input.name() != null) module.setName(input.name());
        if(input.description() != null) module.setDescription(input.description());
        if(input.semester() != null) module.setSemester(input.semester());
        if(input.credits() != null) module.setCredits(input.credits());
        if(input.hours() != null) module.setHours(input.hours());
        if(input.type() != null) module.setType(input.type());
        if(input.prerequisite() != null) module.setPrerequisite(input.prerequisite());
        if(input.maxStudents() != null) module.setMaxStudents(input.maxStudents());
        if(input.mandatory() != null) module.setMandatory(input.mandatory());
        if(input.professorId() != null) module.setProfessor(input.professorId());
    }

    record ModuleInput(
        String code,
        String name,
        String description,
        String courseId, 
        String semester,
        Integer credits,
        Integer hours,
        Module.ModuleType type,
        String professorId,
        String prerequisite,
        Integer maxStudents,
        Boolean mandatory
    ) {}
}