package com.university.auth.controllers;

import com.university.auth.models.User;
import com.university.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    public record LoginRequest(String username, String password) {}
    public record RegisterRequest(String username, String password, String email, String firstName, String lastName, User.Role role) {}
    public record AuthResponse(String token, String username, User.Role role, String message) {}


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.username());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(request.password())) {
                
                String token = UUID.randomUUID().toString();
                
                return ResponseEntity.ok(new AuthResponse(
                    token, 
                    user.getUsername(), 
                    user.getRole(), 
                    "Connexion réussie"
                ));
            }
        }
        return ResponseEntity.status(401).body(Map.of("error", "Identifiant ou mot de passe incorrect"));
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cet utilisateur existe déjà"));
        }

        User newUser = new User(
            null,
            request.username(),
            request.password(),
            request.email(),
            request.firstName(),
            request.lastName(),
            request.role()
        );

        userRepository.save(newUser);
        return ResponseEntity.ok(Map.of("message", "Utilisateur créé avec succès", "userId", newUser.getUsername()));
    }
    

    @GetMapping("/validate/{username}")
    public ResponseEntity<?> validateUser(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(user -> ResponseEntity.ok(Map.of("valid", true, "role", user.getRole())))
                .orElse(ResponseEntity.status(404).body(Map.of("valid", false)));
    }
}