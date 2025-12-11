package com.university.reservations.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent; // ✅ Changement ici
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_room_id", columnList = "room_id"),
    @Index(name = "idx_date", columnList = "reservation_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "L'ID utilisateur est obligatoire")
    @Column(name = "user_id", nullable = false)
    private String userId; 
    
    @NotBlank(message = "Le nom de l'utilisateur est obligatoire")
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @NotNull(message = "L'ID de la salle est obligatoire")
    @Column(name = "room_id", nullable = false)
    private Long roomId;
    
    @Column(name = "room_code")
    private String roomCode;
    
    @NotNull(message = "La date est obligatoire")
    @FutureOrPresent(message = "La date doit être dans le présent ou le futur")
    @Column(name = "reservation_date", nullable = false) 
    private LocalDate date;
    
    @NotNull(message = "Le créneau horaire est obligatoire")
    @Embedded
    @Valid 
    private TimeSlot timeSlot; 
    
    @NotBlank(message = "L'objet de la réservation est obligatoire")
    private String purpose;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(length = 1000)
    private String notes;
    
    @Column(name = "approved_by")
    private String approvedBy; 
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ReservationStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ReservationStatus {
        PENDING("En attente"),
        CONFIRMED("Confirmée"),
        CANCELLED("Annulée"),
        COMPLETED("Terminée"),
        REJECTED("Rejetée");
        
        private final String label;
        
        ReservationStatus(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
    }
}