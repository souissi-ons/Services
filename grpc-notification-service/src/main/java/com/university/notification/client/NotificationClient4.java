package com.university.notification.client;

import com.university.notifications.stubs.AckRequest;
import com.university.notifications.stubs.AckResponse;
import com.university.notifications.stubs.NotificationServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

public class NotificationClient4 {

    public static void main(String[] args) throws IOException, InterruptedException {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        NotificationServiceGrpc.NotificationServiceStub asyncStub =
            NotificationServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AckResponse> responseObserver = new StreamObserver<AckResponse>() {
            @Override
            public void onNext(AckResponse response) {
                System.out.println("<- [BIDIRECTIONNEL] Réponse du Serveur: [Status=" + response.getStatus()
                                   + ", Notifications non lues=" + response.getUnreadCount() + "]");
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Erreur dans le flux bidirectionnel : " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("[BIDIRECTIONNEL] Fin du flux bidirectionnel.");
                latch.countDown();
            }
        };

        StreamObserver<AckRequest> requestObserver = 
                asyncStub.syncNotifications(responseObserver);
        
        System.out.println("[BIDIRECTIONNEL] Démarrage du Flux de Synchronisation: Envoi automatique...");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int counter = 0;
            @Override
            public void run() {
                AckRequest request = AckRequest.newBuilder()
                        .setNotificationId(String.valueOf(1000 + counter))
                        .setUserId("ETU_SYNC")
                        .build();

                requestObserver.onNext(request);
                System.out.println("==========> Client envoie: Ack pour Notification #" + (1000 + counter) + " (utilisateur ETU_SYNC)");
                
                counter++;
                if (counter == 10) {
                    requestObserver.onCompleted();
                    timer.cancel();
                }
            }
        }, 1000, 1000);

        System.out.println("Écoute des réponses en temps réel...");
        latch.await(15, TimeUnit.SECONDS);

        System.out.println("Fermeture du canal.");
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}