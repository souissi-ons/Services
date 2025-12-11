package com.university.notification.client;

import com.university.notifications.stubs.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NotificationClient3 {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        NotificationServiceGrpc.NotificationServiceStub asyncStub =
            NotificationServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<BatchNotificationResponse> responseObserver = new StreamObserver<BatchNotificationResponse>() {
            @Override
            public void onNext(BatchNotificationResponse response) {
                System.out.println(" [CLIENT STREAMING] Réponse du Serveur:");
                System.out.println("   Total envoyé avec succès: " + response.getCountSuccess());
                System.out.println("   Status: " + response.getStatus());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println(" Erreur de streaming client : " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println(" [CLIENT STREAMING] Le serveur a complété le traitement du lot.");
                latch.countDown();
            }
        };

        StreamObserver<SendNotificationRequest> requestObserver =
            asyncStub.sendBatchNotifications(responseObserver);

        System.out.println(" [CLIENT STREAMING] Envoi de 5 notifications en lot...");

        try {
            for (int i = 1; i <= 5; i++) {
                SendNotificationRequest request = SendNotificationRequest.newBuilder()
                        .setUserId("ETU_BATCH_" + i)
                        .setTitle("Rappel Frais #" + i)
                        .setMessage("Rappel de paiement des frais de scolarité (lot #" + i + ")")
                        .setType(NotificationType.GENERAL_INFO)
                        .setPriority(Priority.LOW)
                        .build();
                
                requestObserver.onNext(request);
                System.out.println("   -> Envoi du message #" + i + " pour ETU_BATCH_" + i);
                Thread.sleep(200);
            }
        } catch (RuntimeException | InterruptedException e) {
            requestObserver.onError(e);
            throw new RuntimeException(e);
        }

        requestObserver.onCompleted();
        System.out.println(" [CLIENT STREAMING] Toutes les requêtes envoyées, en attente de la réponse...");

        latch.await(10, TimeUnit.SECONDS);
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}