package com.university.notification.client;

import com.university.notifications.stubs.NotificationServiceGrpc;
import com.university.notifications.stubs.SendNotificationRequest;
import com.university.notifications.stubs.SendNotificationResponse;
import com.university.notifications.stubs.NotificationType;
import com.university.notifications.stubs.Priority;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class NotificationClient1 {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext() 
                .build();

        NotificationServiceGrpc.NotificationServiceBlockingStub blockingStub =
            NotificationServiceGrpc.newBlockingStub(channel);

        SendNotificationRequest request = SendNotificationRequest.newBuilder()
                .setUserId("ETU001")
                .setType(NotificationType.GRADE_PUBLISHED)
                .setTitle("Nouvelle Note Publiée")
                .setMessage("Votre note de projet est disponible pour le module INF301.") 
                .setPriority(Priority.HIGH)
                .build();

        System.out.println("📨 [UNARY] Envoi d'une notification simple pour ETU001...");

        try {
            SendNotificationResponse response = blockingStub.sendNotification(request);

            System.out.println("[UNARY] Réponse reçue:");
            System.out.println("   Succès: " + response.getSuccess());
            System.out.println("   ID Notification: " + response.getNotificationId());
            System.out.println("   Message Serveur: " + response.getMessage()); 

        } catch (Exception e) {
            System.err.println("[UNARY] Erreur d'appel: " + e.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}