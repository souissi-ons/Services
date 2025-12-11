package com.university.reservations.controllers;

import com.university.reservations.models.Equipment;
import com.university.reservations.repositories.EquipmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
@Tag(name = "Équipements", description = "Gestion des équipements")
@CrossOrigin(origins = "*")
public class EquipmentController {
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @GetMapping
    @Operation(summary = "Liste tous les équipements")
    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupère un équipement par ID")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
        return equipmentRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Récupère un équipement par code")
    public ResponseEntity<Equipment> getEquipmentByCode(@PathVariable String code) {
        return equipmentRepository.findByCode(code)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Filtre par type")
    public List<Equipment> getEquipmentByType(@PathVariable String type) {
        return equipmentRepository.findByType(type);
    }
    
    @GetMapping("/available/{type}")
    @Operation(summary = "Liste les équipements disponibles de ce type (Bon/Excellent état)")
    public List<Equipment> getAvailableEquipmentByType(@PathVariable String type) {
        return equipmentRepository.findAvailableEquipmentByType(type);
    }
    
    @PostMapping
    @Operation(summary = "Crée un nouvel équipement")
    public ResponseEntity<Equipment> createEquipment(@Valid @RequestBody Equipment equipment) {
        Equipment saved = equipmentRepository.save(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Met à jour un équipement")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Long id,
                                                     @Valid @RequestBody Equipment equipment) {
        if (!equipmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        equipment.setId(id);
        return ResponseEntity.ok(equipmentRepository.save(equipment));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un équipement")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        if (!equipmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        equipmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/location")
    @Operation(summary = "Change la localisation")
    public ResponseEntity<Equipment> updateLocation(@PathVariable Long id,
                                                    @RequestParam String location) {
        return equipmentRepository.findById(id)
            .map(equipment -> {
                equipment.setCurrentLocation(location);
                return ResponseEntity.ok(equipmentRepository.save(equipment));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}