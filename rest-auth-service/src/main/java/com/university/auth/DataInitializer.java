package com.university.auth;

import com.university.auth.models.User;
import com.university.auth.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository repository) {
        return args -> {
            if (!repository.existsByUsername("ETU001")) {
                repository.save(new User(null, "ETU001", "password", "ahmed@uni.tn", "Ahmed", "Ben Ali", User.Role.STUDENT));
            }
            if (!repository.existsByUsername("PROF001")) {
                repository.save(new User(null, "PROF001", "password", "karim@uni.tn", "Karim", "Mejri", User.Role.PROFESSOR));
            }
            if (!repository.existsByUsername("ADMIN001")) {
                repository.save(new User(null, "ADMIN001", "admin", "admin@uni.tn", "Super", "Admin", User.Role.ADMIN));
            }
            System.out.println("✅ Données d'authentification initialisées (PostgreSQL)");
        };
    }
}