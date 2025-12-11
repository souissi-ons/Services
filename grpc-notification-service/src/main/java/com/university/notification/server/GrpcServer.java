package com.university.notification.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

import com.university.notification.service.NotificationServiceImpl;


public class GrpcServer {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        
        System.out.println("========================================");
        System.out.println("  SERVICE gRPC - NOTIFICATIONS TEMPS RÉEL");
        System.out.println("========================================");
        System.out.println();
        
        Server server = ServerBuilder.forPort(9090)
                .addService(new NotificationServiceImpl())
                .build();
        
        server.start();
        
        System.out.println("Serveur gRPC démarré avec succès !");
        System.out.println("Port: 9090");
        System.out.println();
        System.out.println("Services disponibles :");
        System.out.println("  1️  SendNotification (Unary)");
        System.out.println("  2️  SubscribeToNotifications (Server Streaming)");
        System.out.println("  3️  SendBatchNotifications (Client Streaming)");
        System.out.println("  4️  NotificationStream (Bidirectional Streaming)");
        System.out.println("  5️  GetNotificationStats (Unary)");
        System.out.println("  6️  MarkAsRead (Unary)");
        System.out.println();
        System.out.println("Appuyez sur Ctrl+C pour arrêter le serveur");
        System.out.println("========================================");
            server.awaitTermination();
        }
        }
