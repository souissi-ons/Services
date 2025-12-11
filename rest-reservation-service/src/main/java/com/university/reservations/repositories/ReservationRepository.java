package com.university.reservations.repositories;

import com.university.reservations.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(String userId);
    List<Reservation> findByRoomId(Long roomId);
    List<Reservation> findByDate(LocalDate date);
    
    List<Reservation> findByRoomIdAndDate(Long roomId, LocalDate date);

  
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.roomId = :roomId " +
           "AND r.date = :date " +
           "AND r.status <> 'CANCELLED' " +
           "AND r.status <> 'REJECTED' " +
           "AND (r.timeSlot.startTime < :endTime AND r.timeSlot.endTime > :startTime)")
    List<Reservation> findOverlappingReservations(@Param("roomId") Long roomId,
                                                  @Param("date") LocalDate date,
                                                  @Param("startTime") LocalTime startTime,
                                                  @Param("endTime") LocalTime endTime);
}