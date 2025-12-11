package com.university.archives.service;

import com.university.archives.models.Certification;
import com.university.archives.models.Diplome;
import com.university.archives.models.Transcript;
import com.university.archives.utils.DatabaseConnection;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebService(serviceName = "ArchivesWS")
public class ArchivesService {

    @WebMethod(operationName = "issueDiploma")
    public boolean issueDiploma(@WebParam(name = "diplome") Diplome diplome) {
        String sql = "INSERT INTO diplomes (id, student_id, student_name, level, speciality, issue_date, mention, university) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (diplome.getId() == null) diplome.setId(UUID.randomUUID().toString());

            pstmt.setString(1, diplome.getId());
            pstmt.setString(2, diplome.getStudentId());
            pstmt.setString(3, diplome.getStudentName());
            pstmt.setString(4, diplome.getDiplomeType()); 
            pstmt.setString(5, diplome.getSpeciality());
            pstmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setString(7, diplome.getMention());
            pstmt.setString(8, "Université Centrale");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WebMethod(operationName = "checkDiplomaValidity")
    public boolean checkDiplomaValidity(@WebParam(name = "diplomaId") String diplomaId, 
                                        @WebParam(name = "studentName") String studentName) {
        String sql = "SELECT COUNT(*) FROM diplomes WHERE id = ? AND student_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, diplomaId);
            pstmt.setString(2, studentName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @WebMethod(operationName = "createCertification")
    public Certification createCertification(@WebParam(name = "studentId") String studentId, 
                                             @WebParam(name = "studentName") String name,
                                             @WebParam(name = "type") String type,
                                             @WebParam(name = "purpose") String purpose) {
        Certification cert = new Certification();
        cert.setId(UUID.randomUUID().toString());
        cert.setStudentId(studentId);
        cert.setStudentName(name);
        cert.setCertificationType(type);
        cert.setPurpose(purpose);
        cert.setIssueDate(new java.util.Date());
        cert.setValid(true);

        String sql = "INSERT INTO certifications (id, student_id, student_name, type, issue_date, purpose, valid) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cert.getId());
            pstmt.setString(2, cert.getStudentId());
            pstmt.setString(3, cert.getStudentName());
            pstmt.setString(4, cert.getCertificationType());
            pstmt.setDate(5, new java.sql.Date(cert.getIssueDate().getTime()));
            pstmt.setString(6, cert.getPurpose());
            pstmt.setBoolean(7, true);
            
            pstmt.executeUpdate();
            return cert;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @WebMethod(operationName = "revokeCertification")
    public boolean revokeCertification(@WebParam(name = "certificationId") String id) {
        String sql = "UPDATE certifications SET valid = false WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @WebMethod(operationName = "addTranscript")
    public boolean addTranscript(@WebParam(name = "transcript") Transcript transcript) {
        String sqlHeader = "INSERT INTO transcripts (id, student_id, semester, academic_year, gpa) VALUES (?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO transcript_grades (transcript_id, module_name, score, coefficient) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (transcript.getId() == null) transcript.setId(UUID.randomUUID().toString());
            
            try (PreparedStatement pstmt = conn.prepareStatement(sqlHeader)) {
                pstmt.setString(1, transcript.getId());
                pstmt.setString(2, transcript.getStudentId());
                pstmt.setString(3, transcript.getSemester());
                pstmt.setString(4, transcript.getAcademicYear());
                pstmt.setDouble(5, transcript.getGpa());
                pstmt.executeUpdate();
            }

            if (transcript.getGrades() != null && !transcript.getGrades().isEmpty()) {
                try (PreparedStatement pstmt = conn.prepareStatement(sqlDetail)) {
                    for (Transcript.Grade grade : transcript.getGrades()) {
                        pstmt.setString(1, transcript.getId());
                        pstmt.setString(2, grade.getModuleName());
                        pstmt.setDouble(3, grade.getNote());
                        pstmt.setDouble(4, 1.0); 
                        pstmt.addBatch(); 
                    }
                    pstmt.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Rollback transaction...");
                    conn.rollback(); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
            }
        }
    }
}