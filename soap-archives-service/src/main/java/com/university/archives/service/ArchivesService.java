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

    // ==================== DIPLÔMES ====================
    
    @WebMethod(operationName = "issueDiploma")
    public Diplome issueDiploma(@WebParam(name = "diplome") Diplome diplome) {
        String sql = "INSERT INTO diplomes (id, student_id, student_name, level, speciality, issue_date, mention, university, archived, academic_year, final_grade) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (diplome.getId() == null) diplome.setId(UUID.randomUUID().toString());
            if (diplome.getIssueDate() == null) diplome.setIssueDate(new java.util.Date());
            
            pstmt.setString(1, diplome.getId());
            pstmt.setString(2, diplome.getStudentId());
            pstmt.setString(3, diplome.getStudentName());
            pstmt.setString(4, diplome.getDiplomeType());
            pstmt.setString(5, diplome.getSpeciality());
            pstmt.setDate(6, new java.sql.Date(diplome.getIssueDate().getTime()));
            pstmt.setString(7, diplome.getMention());
            pstmt.setString(8, diplome.getInstitution() != null ? diplome.getInstitution() : "Université Centrale");
            pstmt.setBoolean(9, true);
            pstmt.setString(10, diplome.getAcademicYear());
            pstmt.setDouble(11, diplome.getFinalGrade() != null ? diplome.getFinalGrade() : 0.0);

            pstmt.executeUpdate();
            return diplome;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'émission du diplôme: " + e.getMessage());
        }
    }

    @WebMethod(operationName = "getDiplomaByStudent")
    public List<Diplome> getDiplomaByStudent(@WebParam(name = "studentId") String studentId) {
        List<Diplome> diplomes = new ArrayList<>();
        String sql = "SELECT * FROM diplomes WHERE student_id = ? ORDER BY issue_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Diplome d = new Diplome();
                d.setId(rs.getString("id"));
                d.setStudentId(rs.getString("student_id"));
                d.setStudentName(rs.getString("student_name"));
                d.setDiplomeType(rs.getString("level"));
                d.setSpeciality(rs.getString("speciality"));
                d.setIssueDate(rs.getDate("issue_date"));
                d.setMention(rs.getString("mention"));
                d.setInstitution(rs.getString("university"));
                d.setArchived(rs.getBoolean("archived"));
                d.setAcademicYear(rs.getString("academic_year"));
                d.setFinalGrade(rs.getDouble("final_grade"));
                diplomes.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return diplomes;
    }

    @WebMethod(operationName = "checkDiplomaValidity")
    public boolean checkDiplomaValidity(@WebParam(name = "diplomaId") String diplomaId, 
                                        @WebParam(name = "studentName") String studentName) {
        String sql = "SELECT COUNT(*) FROM diplomes WHERE id = ? AND student_name = ? AND archived = true";
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

    // ==================== CERTIFICATIONS ====================

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
            throw new RuntimeException("Erreur création certification: " + e.getMessage());
        }
    }

    @WebMethod(operationName = "getCertificationsByStudent")
    public List<Certification> getCertificationsByStudent(@WebParam(name = "studentId") String studentId) {
        List<Certification> certifications = new ArrayList<>();
        String sql = "SELECT * FROM certifications WHERE student_id = ? ORDER BY issue_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Certification c = new Certification();
                c.setId(rs.getString("id"));
                c.setStudentId(rs.getString("student_id"));
                c.setStudentName(rs.getString("student_name"));
                c.setCertificationType(rs.getString("type"));
                c.setIssueDate(rs.getDate("issue_date"));
                c.setPurpose(rs.getString("purpose"));
                c.setValid(rs.getBoolean("valid"));
                certifications.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return certifications;
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

    // ==================== TRANSCRIPTS ====================

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
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true); 
                    conn.close(); 
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @WebMethod(operationName = "getTranscriptsByStudent")
    public List<Transcript> getTranscriptsByStudent(@WebParam(name = "studentId") String studentId) {
        List<Transcript> transcripts = new ArrayList<>();
        String sql = "SELECT * FROM transcripts WHERE student_id = ? ORDER BY academic_year DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Transcript t = new Transcript();
                t.setId(rs.getString("id"));
                t.setStudentId(rs.getString("student_id"));
                t.setSemester(rs.getString("semester"));
                t.setAcademicYear(rs.getString("academic_year"));
                t.setGpa(rs.getDouble("gpa"));
                
                // Charger les notes
                String sqlGrades = "SELECT * FROM transcript_grades WHERE transcript_id = ?";
                try (PreparedStatement pstmtGrades = conn.prepareStatement(sqlGrades)) {
                    pstmtGrades.setString(1, t.getId());
                    ResultSet rsGrades = pstmtGrades.executeQuery();
                    List<Transcript.Grade> grades = new ArrayList<>();
                    while (rsGrades.next()) {
                        Transcript.Grade g = new Transcript.Grade();
                        g.setModuleName(rsGrades.getString("module_name"));
                        g.setNote(rsGrades.getDouble("score"));
                        grades.add(g);
                    }
                    t.setGrades(grades);
                }
                
                transcripts.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transcripts;
    }

    @WebMethod(operationName = "generateAcademicReport")
    public String generateAcademicReport(@WebParam(name = "studentId") String studentId) {
        StringBuilder report = new StringBuilder();
        report.append("=== RAPPORT ACADÉMIQUE ===\n\n");
        
        // Diplômes
        List<Diplome> diplomes = getDiplomaByStudent(studentId);
        report.append("DIPLÔMES: ").append(diplomes.size()).append("\n");
        for (Diplome d : diplomes) {
            report.append("  - ").append(d.getDiplomeType())
                  .append(" en ").append(d.getSpeciality())
                  .append(" (").append(d.getMention()).append(")\n");
        }
        
        // Transcripts
        List<Transcript> transcripts = getTranscriptsByStudent(studentId);
        report.append("\nRELEVÉS DE NOTES: ").append(transcripts.size()).append("\n");
        for (Transcript t : transcripts) {
            report.append("  - ").append(t.getAcademicYear())
                  .append(" / ").append(t.getSemester())
                  .append(" : GPA = ").append(String.format("%.2f", t.getGpa())).append("\n");
        }
        
        // Certifications
        List<Certification> certs = getCertificationsByStudent(studentId);
        report.append("\nCERTIFICATIONS: ").append(certs.size()).append("\n");
        for (Certification c : certs) {
            report.append("  - ").append(c.getCertificationType())
                  .append(" (").append(c.isValid() ? "Valide" : "Révoquée").append(")\n");
        }
        
        return report.toString();
    }
}