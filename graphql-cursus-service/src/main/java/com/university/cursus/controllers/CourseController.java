package com.university.cursus.controllers;

import com.university.cursus.models.Course;
import com.university.cursus.models.Module;
import com.university.cursus.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class CourseController {
    
    @Autowired
    private CourseRepository courseRepository;
    
    
    @QueryMapping
    public Course courseById(@Argument Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course introuvable avec l'ID : " + id));
    }
    
    @QueryMapping
    public Course courseByCode(@Argument String code) {
        return courseRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Course introuvable avec le code : " + code));
    }
    
    @QueryMapping
    public List<Course> allCourses() {
        return courseRepository.findAll();
    }
    
    @QueryMapping
    public List<Course> coursesByLevel(@Argument String level) {
        return courseRepository.findByLevel(level);
    }
    

    @MutationMapping
    @Transactional
    public Course createCourse(@Argument CourseInput input) {
        if (courseRepository.findByCode(input.code()).isPresent()) {
            throw new RuntimeException("Un cours avec ce code existe déjà : " + input.code());
        }

        Course course = new Course();
        updateCourseFromInput(course, input);
        return courseRepository.save(course);
    }

    @MutationMapping
    @Transactional
    public Course updateCourse(@Argument Long id, @Argument CourseInput input) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course introuvable ID : " + id));
        
        if (!course.getCode().equals(input.code()) && courseRepository.findByCode(input.code()).isPresent()) {
             throw new RuntimeException("Le code " + input.code() + " est déjà utilisé par un autre cours.");
        }

        updateCourseFromInput(course, input);
        return courseRepository.save(course);
    }

    @MutationMapping
    @Transactional
    public Boolean deleteCourse(@Argument Long id) {
        if (!courseRepository.existsById(id)) {
            return false;
        }
        courseRepository.deleteById(id);
        return true;
    }

    
    @SchemaMapping(typeName = "Course", field = "modules")
    public List<Module> modules(Course course) {
        return course.getModules();
    }


    private void updateCourseFromInput(Course course, CourseInput input) {
        course.setCode(input.code());
        course.setName(input.name());
        course.setDescription(input.description());
        course.setLevel(input.level());
        course.setSpeciality(input.speciality());
        course.setDurationYears(input.durationYears());
        course.setTotalCredits(input.totalCredits());
        course.setCoordinator(input.coordinator());
        course.setActive(input.active() != null ? input.active() : true);
    }

    record CourseInput(
        String code,
        String name,
        String description,
        String level,
        String speciality,
        Integer durationYears,
        Integer totalCredits,
        String coordinator,
        Boolean active
    ) {}
}