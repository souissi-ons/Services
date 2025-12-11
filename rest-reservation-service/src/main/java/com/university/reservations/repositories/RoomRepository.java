package com.university.reservations.repositories;

import com.university.reservations.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    Optional<Room> findByCode(String code);
    
    List<Room> findByType(String type);
    
    List<Room> findByAvailable(Boolean available);
    
    List<Room> findByBuilding(String building);
    
    List<Room> findByCapacityGreaterThanEqual(Integer capacity);
    
    List<Room> findByBuildingAndFloor(String building, Integer floor);
    
    @Query("SELECT r FROM Room r WHERE r.hasProjector = true AND r.available = true")
    List<Room> findAvailableRoomsWithProjector();
    
    @Query("SELECT r FROM Room r WHERE r.capacity >= :minCapacity AND r.type = :type AND r.available = true")
    List<Room> findAvailableRoomsByCapacityAndType(@Param("minCapacity") Integer minCapacity, 
                                                    @Param("type") String type);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.type = :type")
    Long countByType(@Param("type") String type);
}