package com.university.reservations.controllers;

import com.university.reservations.models.Room;
import com.university.reservations.repositories.RoomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min; // Import pour la validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated; // Ajout pour la validation des RequestParam/PathVariable
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Salles", description = "Gestion des salles universitaires")
@CrossOrigin(origins = "*")
@Validated 
public class RoomController {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @GetMapping
    @Operation(summary = "Liste toutes les salles")
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupère une salle par ID")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Récupère une salle par code")
    public ResponseEntity<Room> getRoomByCode(@PathVariable String code) {
        return roomRepository.findByCode(code)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Filtre les salles par type")
    public List<Room> getRoomsByType(@PathVariable String type) {
        return roomRepository.findByType(type);
    }
    
    @GetMapping("/available")
    @Operation(summary = "Liste les salles disponibles")
    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailable(true);
    }
    
    @GetMapping("/building/{building}")
    @Operation(summary = "Filtre par bâtiment")
    public List<Room> getRoomsByBuilding(@PathVariable String building) {
        return roomRepository.findByBuilding(building);
    }
    
    @GetMapping("/capacity/{minCapacity}")
    @Operation(summary = "Salles avec capacité minimale")
    public List<Room> getRoomsByMinCapacity(@PathVariable @Min(value = 1) int minCapacity) {
        return roomRepository.findByCapacityGreaterThanEqual(minCapacity);
    }
    

    @GetMapping("/search")
    @Operation(summary = "Recherche combinée : salles disponibles par critères (type, capacité, projecteur)")
    public List<Room> searchRooms(@RequestParam(required = false) String type,
                                  @RequestParam(required = false, defaultValue = "1") @Min(value = 1) Integer minCapacity,
                                  @RequestParam(required = false) Boolean hasProjector) {
        
        if (hasProjector != null && hasProjector) {
            return roomRepository.findAvailableRoomsWithProjector();
        }
        if (type != null && minCapacity > 1) {
            return roomRepository.findAvailableRoomsByCapacityAndType(minCapacity, type);
        }
        if (type != null) {
            return roomRepository.findByType(type);
        }
        if (minCapacity > 1) {
            return roomRepository.findByCapacityGreaterThanEqual(minCapacity);
        }
        return roomRepository.findByAvailable(true); 
    }

    
    @PostMapping
    @Operation(summary = "Crée une nouvelle salle")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        Room saved = roomRepository.save(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Met à jour une salle")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, 
                                           @Valid @RequestBody Room room) {
        if (!roomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        room.setId(id);
        return ResponseEntity.ok(roomRepository.save(room));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une salle")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (!roomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        roomRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/availability")
    @Operation(summary = "Change la disponibilité (true/false)")
    public ResponseEntity<Room> toggleAvailability(@PathVariable Long id) {
        return roomRepository.findById(id)
            .map(room -> {
                room.setAvailable(!room.getAvailable()); 
                return ResponseEntity.ok(roomRepository.save(room));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}