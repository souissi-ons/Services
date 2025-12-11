package com.university.cursus.repositories;

import com.university.cursus.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByCode(String code);
    List<Module> findByCourseId(Long courseId);
    List<Module> findBySemester(String semester);
    List<Module> findByProfessor(String professor);
    List<Module> findByType(Module.ModuleType type);
}