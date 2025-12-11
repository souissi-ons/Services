package com.university.archives;

import com.university.archives.service.ArchivesService;
import com.university.archives.utils.DatabaseConnection;
import jakarta.xml.ws.Endpoint;

import java.sql.Connection;
import java.sql.Statement;

public class SoapArchivesService {

	 public static void main(String[] args) {
	        initializeDatabase();

	        String url = "http://0.0.0.0:8081/archives";	        
	        System.out.println("Démarrage du service SOAP Archives...");
	        
	        try {
	            Endpoint.publish(url, new ArchivesService());
	            System.out.println("Service SOAP en ligne sur : " + url + "?wsdl");
	        } catch (Exception e) {
	            System.err.println("Erreur critique : " + e.getMessage());
	            e.printStackTrace();
	        }
	    }

    private static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS certifications (
                    id VARCHAR(50) PRIMARY KEY,
                    student_id VARCHAR(50) NOT NULL,
                    student_name VARCHAR(100),
                    type VARCHAR(50),
                    issue_date DATE,
                    purpose VARCHAR(100),
                    valid BOOLEAN DEFAULT TRUE
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS diplomes (
                    id VARCHAR(50) PRIMARY KEY,
                    student_id VARCHAR(50) NOT NULL,
                    student_name VARCHAR(100),
                    level VARCHAR(50),
                    speciality VARCHAR(100),
                    issue_date DATE,
                    mention VARCHAR(50),
                    university VARCHAR(100)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transcripts (
                    id VARCHAR(50) PRIMARY KEY,
                    student_id VARCHAR(50) NOT NULL,
                    semester VARCHAR(20),
                    academic_year VARCHAR(20),
                    gpa DOUBLE PRECISION
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transcript_grades (
                    id SERIAL PRIMARY KEY,
                    transcript_id VARCHAR(50) REFERENCES transcripts(id),
                    module_name VARCHAR(100),
                    score DOUBLE PRECISION,
                    coefficient DOUBLE PRECISION
                )
            """);

            System.out.println("Schéma de base de données PostgreSQL vérifié/créé.");

        } catch (Exception e) {
            System.err.println("Erreur initialisation BDD (peut-être en cours de démarrage): " + e.getMessage());
        }
    }
}