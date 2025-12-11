package com.university.reservations.controllers;

import com.university.reservations.models.Reservation;
import com.university.reservations.models.TimeSlot;
import com.university.reservations.repositories.ReservationRepository;
import com.university.reservations.repositories.RoomRepository;
import com.university.reservations.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Réservations", description = "Gestion des réservations")
@CrossOrigin(origins = "*")
public class ReservationController {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomRepository roomRepository;
    
    @GetMapping
    @Operation(summary = "Liste toutes les réservations")
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupère une réservation par ID")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupère les réservations par utilisateur")
    public List<Reservation> getReservationsByUserId(@PathVariable String userId) {
        return reservationRepository.findByUserId(userId);
    }
    
    @PostMapping
    @Operation(summary = "Crée une nouvelle réservation")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody Reservation reservation) {
        try {
            Reservation saved = reservationService.createReservation(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalStateException e) {            // Conflit de disponibilité (409 Conflict)
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException | SecurityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Met à jour une réservation")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id,
                                                         @Valid @RequestBody Reservation reservation) {
        return reservationRepository.findById(id)
            .map(existing -> {
                boolean timeChanged = !existing.getDate().equals(reservation.getDate()) ||
                                      !existing.getTimeSlot().equals(reservation.getTimeSlot()) ||
                                      !existing.getRoomId().equals(reservation.getRoomId());

                if (timeChanged) {
                    boolean available = reservationService.isRoomAvailable(
                        reservation.getRoomId(), 
                        reservation.getDate(), 
                        reservation.getTimeSlot()
                    );
                    
                    if (!available) {
                         throw new ResponseStatusException(HttpStatus.CONFLICT, "Le nouveau créneau demandé est indisponible.");
                    }
                }

                existing.setDate(reservation.getDate());
                existing.setTimeSlot(reservation.getTimeSlot());
                existing.setRoomId(reservation.getRoomId());
                existing.setPurpose(reservation.getPurpose());
                
                return ResponseEntity.ok(reservationRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une réservation")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        if (!reservationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Change le statut de la réservation")
    public ResponseEntity<Reservation> updateStatus(@PathVariable Long id,
                                                    @RequestParam Reservation.ReservationStatus status) {
        return reservationRepository.findById(id)
            .map(reservation -> {
                reservation.setStatus(status);
                
                if (status == Reservation.ReservationStatus.CONFIRMED && reservation.getApprovedAt() == null) {
                    reservation.setApprovedAt(LocalDateTime.now());
                    reservation.setApprovedBy("ADMIN_SYSTEM"); 
                }
                
                return ResponseEntity.ok(reservationRepository.save(reservation));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/check-availability")
    @Operation(summary = "Vérifie la disponibilité de la salle pour un créneau")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody TimeSlot timeSlot) { 
        
        if (!roomRepository.existsById(roomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Salle non trouvée avec ID: " + roomId);
        }
            
        boolean available = reservationService.isRoomAvailable(roomId, date, timeSlot); 
        return ResponseEntity.ok(Map.of("available", available));
    }
}