package com.university.archives.models;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "certification")
@XmlAccessorType(XmlAccessType.FIELD)
public class Certification {
    
    @XmlElement(required = true)
    private String id;
    
    @XmlElement(required = true)
    private String studentId;
    
    @XmlElement(required = true)
    private String studentName;
    
    @XmlElement(required = true)
    private String certificationType; 
    
    @XmlElement(required = true)
    private Date issueDate;
    
    @XmlElement
    private Date expiryDate; 
    
    @XmlElement(required = true)
    private String purpose; 
    
    @XmlElement
    private String deliveredBy; 
    
    @XmlElement
    private boolean valid; 
        
    @XmlElement
    private String academicYear;
    
    @XmlElement
    private String level; 
    
    @XmlElement
    private String speciality;
    
    @XmlElement
    private String notes;
    
    @XmlElement
    private int copiesIssued; 
}