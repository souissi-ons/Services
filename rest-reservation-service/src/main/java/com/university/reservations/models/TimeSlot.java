package com.university.reservations.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull; // <-- Import nécessaire pour @NotNull
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {
    
    @NotNull(message = "L'heure de début est obligatoire")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @NotNull(message = "L'heure de fin est obligatoire")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    

    public boolean overlapsWith(TimeSlot other) {
        if (other == null) return false;
        return this.startTime.isBefore(other.endTime) && 
               this.endTime.isAfter(other.startTime);
    }
    

    public long getDurationInMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }
    

    public boolean isValid() {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }
}