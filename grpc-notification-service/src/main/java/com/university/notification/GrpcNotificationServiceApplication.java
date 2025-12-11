package com.university.notification;

import com.university.notification.service.NotificationServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GrpcNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcNotificationServiceApplication.class, args);
    }

    @Autowired
    private NotificationServiceImpl notificationService; 

    @Bean
    public CommandLineRunner startGrpcServer() {
        return args -> {
            Server server = ServerBuilder.forPort(9090)
                    .addService(notificationService) 
                    .build();

            server.start();
            System.out.println("✅ Serveur gRPC (Notification) démarré sur le port 9090");
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("🛑 Arrêt du serveur gRPC...");
                server.shutdown();
            }));
            
            server.awaitTermination();
        };
    }
}