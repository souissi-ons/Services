package com.university.cursus;

import com.university.cursus.models.*;
import com.university.cursus.models.Module;
import com.university.cursus.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class GraphqlCursusServiceApplication {

    public static void main(String[] args) {
    	SpringApplication.run(GraphqlCursusServiceApplication.class, args);
        System.out.println("✅ Service GraphQL démarré !");
    }

  
    @Bean
    CommandLineRunner initDatabase(StudentRepository studentRepo,
                                   CourseRepository courseRepo,
                                   ModuleRepository moduleRepo,
                                   EnrollmentRepository enrollmentRepo,
                                   ProfessorRepository professorRepo) {
        return args -> {
        	// AVANT (Probablement)
        	if (courseRepo.count() > 0) {
        	    return; // Si on a des cours, on saute l'initialisation
        	}

        	// APRÈS (Correction) : On vérifie le premier jeu de données inséré
        	if (professorRepo.count() > 0) {
        	    System.out.println("⚠️ Données de test déjà présentes. Initialisation ignorée.");
        	    return;
        	}
            System.out.println("🚀 Base vide détectée. Insertion des données de test...");

            Professor p1 = new Professor(null, "PROF001", "Ali", "Benali", "ali.benali@univ.tn", "Informatique", Professor.ProfessorStatus.FULL_TIME, null);
            Professor p2 = new Professor(null, "PROF002", "Fatma", "Gharbi", "fatma.gharbi@univ.tn", "Mathématiques", Professor.ProfessorStatus.RESEARCHER, null);
            professorRepo.saveAll(Arrays.asList(p1, p2));

            Course c1 = new Course();
            c1.setCode("GINF3");
            c1.setName("Licence Génie Informatique");
            c1.setDescription("Ingénierie logicielle");
            c1.setLevel("LICENCE");
            c1.setSpeciality("GL");
            c1.setDurationYears(3);
            c1.setTotalCredits(180);
            c1.setCoordinator("Dr. Salah");
            c1.setActive(true);

            courseRepo.save(c1); 

            Module m1 = new Module();
            m1.setCode("INF101");
            m1.setName("Algorithmique Avancée");
            m1.setDescription("Complexité et graphes");
            m1.setCourse(c1);
            m1.setSemester("S1");
            m1.setCredits(5);
            m1.setHours(45);
            m1.setType(Module.ModuleType.COURS);
            m1.setProfessor(p1.getProfessorId());
            m1.setMaxStudents(30);
            m1.setMandatory(true);

            moduleRepo.save(m1); 

            Student s1 = new Student();
            s1.setStudentId("ETU001");
            s1.setFirstName("Sami");
            s1.setLastName("Trabelsi");
            s1.setEmail("sami@test.com");
            s1.setBirthDate(LocalDate.of(2000, 1, 1));
            s1.setLevel("L3");
            s1.setSpeciality("GL");
            s1.setEnrollmentDate(LocalDate.now());
            s1.setStatus(Student.StudentStatus.ACTIVE);

            studentRepo.save(s1); 

            Enrollment e1 = new Enrollment();
            e1.setStudent(s1);
            e1.setModule(m1);
            e1.setAcademicYear("2024-2025");
            e1.setEnrollmentDate(LocalDateTime.now());
            e1.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
            
            enrollmentRepo.save(e1);

            System.out.println("✅ Données de test insérées.");
        };
    }
}