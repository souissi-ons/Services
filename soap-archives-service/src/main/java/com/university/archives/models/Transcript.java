package com.university.archives.models;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Transcript {
    
    private String id;
    private String studentId;
    private String semester;
    private String academicYear;
    private double gpa;
    
    @XmlElementWrapper(name = "grades")
    @XmlElement(name = "grade")
    private List<Grade> grades;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Grade {
        private String moduleName;
        private double note;
    }
}