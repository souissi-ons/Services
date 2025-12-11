package com.university.reservations.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate; // Import corrigé
import java.time.LocalDateTime;


@Entity
@Table(name = "equipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le code est obligatoire")
    @Column(unique = true, nullable = false)
    private String code; // PROJ001, PC042, CAM003
    
    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Le type est obligatoire")
    @Column(nullable = false)
    private String type; 
    
    private String brand; 
    
    private String model;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentState state = EquipmentState.BON;
    
    @Column(name = "current_location")
    private String currentLocation; 
    
    private String notes;
    
    @Column(name = "purchase_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate; 
    
    @Column(name = "warranty_expiry")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyExpiry; 
    
    private Double price;
    
    @Column(name = "serial_number")
    private String serialNumber;
    
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
    

    public enum EquipmentState {
        EXCELLENT("Excellent état"),
        BON("Bon état"),
        MOYEN("État moyen"),
        MAUVAIS("Mauvais état"),
        EN_REPARATION("En réparation"),
        HORS_SERVICE("Hors service");
        
        private final String label;
        
        EquipmentState(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
    }
}