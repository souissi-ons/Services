package com.university.archives.models;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "diplome")
@XmlAccessorType(XmlAccessType.FIELD)
public class Diplome {
    
    @XmlElement(required = true)
    private String id;
    
    @XmlElement(required = true)
    private String studentId;
    
    @XmlElement(required = true)
    private String studentName;
    
    @XmlElement(required = true)
    private String diplomeType; 
    
    @XmlElement(required = true)
    private String speciality; 
    
    @XmlElement(required = true)
    private Date issueDate;
    
    @XmlElement(required = true)
    private String mention; 
    
    @XmlElement(required = true)
    private boolean archived;
    
    @XmlElement
    private String institution;
    
    @XmlElement
    private String academicYear;
    
    @XmlElement
    private Double finalGrade; 
    
    @XmlElement
    private String jury; 
    
    @XmlElement
    private String deliveryLocation;
}