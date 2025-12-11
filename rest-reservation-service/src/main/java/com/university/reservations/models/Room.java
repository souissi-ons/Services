package com.university.reservations.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le code de la salle est obligatoire")
    @Column(unique = true, nullable = false)
    private String code; // A101, B204, C305
    
    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Le type est obligatoire")
    @Column(nullable = false)
    private String type;
    
    @Min(value = 1, message = "La capacité doit être au moins 1")
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(nullable = false)
    private String building;
    
    @Column(nullable = false)
    private Integer floor;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @ElementCollection
    @CollectionTable(name = "room_equipments", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "equipment")
    private List<String> equipments = new ArrayList<>(); 
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "has_wifi")
    private Boolean hasWifi = true;
    
    @Column(name = "has_whiteboard")
    private Boolean hasWhiteboard = true;
    
    @Column(name = "has_projector")
    private Boolean hasProjector = false;
    
    @Column(name = "accessible_pmr") 
    private Boolean accessiblePMR = false;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}