package com.university.reservations.services;

import com.university.reservations.models.Reservation;
import com.university.reservations.models.Room;
import com.university.reservations.models.TimeSlot;
import com.university.reservations.models.Reservation.ReservationStatus;
import com.university.reservations.repositories.ReservationRepository;
import com.university.reservations.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate; // Import manquant
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    public boolean isRoomAvailable(Long roomId, LocalDate date, TimeSlot timeSlot) {
        List<Reservation> conflicts = reservationRepository.findOverlappingReservations(
                roomId,
                date,
                timeSlot.getStartTime(),
                timeSlot.getEndTime()
        );
        return conflicts.isEmpty();
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        Room room = roomRepository.findById(reservation.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable ID: " + reservation.getRoomId()));

        if (!room.getAvailable()) {
            throw new IllegalStateException("La salle " + room.getName() + " est fermée.");
        }

        if (reservation.getUserId().startsWith("ETU")) {
            throw new SecurityException("Accès refusé : Les étudiants ne peuvent pas réserver.");
        }

        if (!isRoomAvailable(reservation.getRoomId(), reservation.getDate(), reservation.getTimeSlot())) {
             throw new IllegalStateException("Conflit : La salle est déjà occupée sur ce créneau.");
        }

        reservation.setRoomCode(room.getCode());
        reservation.setId(null); 
        
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.CONFIRMED); 
        }

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            reservation.setApprovedAt(LocalDateTime.now());
            reservation.setApprovedBy("SYSTEM");
        }

        return reservationRepository.save(reservation);
    }
}