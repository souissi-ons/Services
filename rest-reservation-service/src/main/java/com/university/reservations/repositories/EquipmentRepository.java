package com.university.reservations.repositories;

import com.university.reservations.models.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
    Optional<Equipment> findByCode(String code);
    
    List<Equipment> findByType(String type);
    
    List<Equipment> findByAvailable(Boolean available);
    
    List<Equipment> findByState(Equipment.EquipmentState state);
    
    List<Equipment> findByCurrentLocation(String location);
    
    List<Equipment> findByBrand(String brand);
    
    @Query("SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true " + 
           "AND (e.state = 'BON' OR e.state = 'EXCELLENT')") 
    List<Equipment> findAvailableEquipmentByType(@Param("type") String type);
    
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.state = :state")
    Long countByState(@Param("state") Equipment.EquipmentState state);
    
    @Query("SELECT e FROM Equipment e WHERE e.warrantyExpiry < CURRENT_DATE")
    List<Equipment> findExpiredWarrantyEquipment();
}