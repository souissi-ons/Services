package com.university.reservations;

import com.university.reservations.models.*;
import com.university.reservations.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class RestReservationServiceApplication {

    public static void main(String[] args) {
    	SpringApplication.run(RestReservationServiceApplication.class, args);
        System.out.println("Service Réservation démarré !");
    }

    @Bean
    CommandLineRunner initDatabase(RoomRepository roomRepo,
                                   EquipmentRepository equipRepo,
                                   ReservationRepository resRepo) {
        return args -> {
            if (roomRepo.count() > 0) return;

            System.out.println("Initialisation des données de réservation...");

            Room r1 = new Room();
            r1.setCode("A101"); r1.setName("Salle Turing"); r1.setType("AMPHI");
            r1.setCapacity(100); r1.setBuilding("Batiment A"); r1.setFloor(1);
            r1.setHasProjector(true); r1.setHasWifi(true);
            
            Room r2 = new Room();
            r2.setCode("B202"); r2.setName("Labo Curie"); r2.setType("LABORATOIRE");
            r2.setCapacity(20); r2.setBuilding("Batiment B"); r2.setFloor(2);
            r2.setHasProjector(false); r2.setHasWifi(true);

            roomRepo.saveAll(Arrays.asList(r1, r2));

            Equipment e1 = new Equipment(null, "PROJ-01", "Projecteur Epson", "PROJECTEUR", "Epson", "X500", true, Equipment.EquipmentState.BON, "Dispo", "Salle Stock", null, null, 500.0, "SN123", null, null);
            equipRepo.save(e1);

            
            Reservation res1 = new Reservation();
            res1.setUserId("PROF_JAVA");
            res1.setUserName("M. Java");
            res1.setRoomId(r1.getId());
            res1.setRoomCode(r1.getCode());
            res1.setDate(LocalDate.now().plusDays(1));
            res1.setTimeSlot(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0)));
            res1.setPurpose("Cours Magistral Spring");
            res1.setStatus(Reservation.ReservationStatus.CONFIRMED);
            
            resRepo.save(res1);

            System.out.println("Données insérées : Salle A101 occupée demain 10h-12h.");
        };
    }
}