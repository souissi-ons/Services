package com.university.cursus.controllers;

import com.university.cursus.models.Module;
import com.university.cursus.models.Professor;
import com.university.cursus.models.Professor.ProfessorStatus;
import com.university.cursus.repositories.ModuleRepository;
import com.university.cursus.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProfessorController {

    @Autowired private ProfessorRepository professorRepository;
    @Autowired private ModuleRepository moduleRepository;

    @QueryMapping
    public Professor professorByProfessorId(@Argument String professorId) {
        return professorRepository.findByProfessorId(professorId).orElse(null);
    }
    
    @QueryMapping
    public List<Professor> allProfessors() {
        return professorRepository.findAll();
    }

    @MutationMapping
    public Professor createProfessor(@Argument ProfessorInput input) {
        if(professorRepository.findByProfessorId(input.professorId()).isPresent()) {
            throw new RuntimeException("Professeur déjà existant avec l'ID: " + input.professorId());
        }

        Professor prof = new Professor();
        prof.setProfessorId(input.professorId());
        prof.setFirstName(input.firstName());
        prof.setLastName(input.lastName());
        prof.setEmail(input.email());
        prof.setDepartment(input.department());
        prof.setStatus(input.status() != null ? input.status() : ProfessorStatus.FULL_TIME);

        return professorRepository.save(prof);
    }

    @MutationMapping
    public Professor updateProfessor(@Argument Long id, @Argument ProfessorInput input) {
        Professor professor = professorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Professeur introuvable ID: " + id));

        if(input.firstName() != null) professor.setFirstName(input.firstName());
        if(input.lastName() != null) professor.setLastName(input.lastName());
        if(input.email() != null) professor.setEmail(input.email());
        if(input.department() != null) professor.setDepartment(input.department());
        if(input.status() != null) professor.setStatus(input.status());

        return professorRepository.save(professor);
    }

    @MutationMapping
    public Boolean deleteProfessor(@Argument Long id) {
        if(professorRepository.existsById(id)) {
            professorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @SchemaMapping(typeName = "Professor", field = "modulesTaught")
    public List<Module> modulesTaught(Professor professor) {
        return moduleRepository.findByProfessor(professor.getProfessorId());
    }
    
    @SchemaMapping(typeName = "Module", field = "professorDetails")
    public Professor professorDetails(Module module) {
        if (module.getProfessor() == null || module.getProfessor().isEmpty()) {
            return null;
        }
        return professorRepository.findByProfessorId(module.getProfessor()).orElse(null);
    }

    record ProfessorInput(
        String professorId,
        String firstName,
        String lastName,
        String email,
        String department,
        ProfessorStatus status
    ) {}
}