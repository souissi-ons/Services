package com.university.notification.client;

import com.university.notifications.stubs.NotificationServiceGrpc;
import com.university.notifications.stubs.SubscribeRequest;
import com.university.notifications.stubs.Notification;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class NotificationClient2 {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        NotificationServiceGrpc.NotificationServiceBlockingStub blockingStub =
            NotificationServiceGrpc.newBlockingStub(channel);

        SubscribeRequest request = SubscribeRequest.newBuilder()
                .setUserId("ETU002")
                .build();

        System.out.println("[SERVER STREAMING] Abonnement aux notifications pour ETU002...");

        try {
            Iterator<Notification> notifications = blockingStub.subscribeToNotifications(request);

            int count = 0;
            while (notifications.hasNext()) {
                Notification notification = notifications.next();
                count++;
                System.out.println("   Notification #" + count + " reçue:");
                System.out.println("      ID: " + notification.getId());
                System.out.println("      Type: " + notification.getType());
                System.out.println("      Priorité: " + notification.getPriority());
                System.out.println("      Titre: '" + notification.getTitle() + "'");
                System.out.println("      Message: '" + notification.getMessage() + "'");
                System.out.println("      Timestamp: " + notification.getTimestamp());
                System.out.println();
                
                Thread.sleep(500);
            }
            System.out.println("[SERVER STREAMING] Fin du flux du serveur.");

        } catch (Exception e) {
            System.err.println("[SERVER STREAMING] Connexion interrompue : " + e.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}