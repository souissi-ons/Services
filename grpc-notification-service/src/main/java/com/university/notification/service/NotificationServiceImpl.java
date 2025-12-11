package com.university.notification.service;

import com.university.notification.models.NotificationEntity;
import com.university.notification.repositories.NotificationRepository;
import com.university.notifications.stubs.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationServiceImpl extends NotificationServiceGrpc.NotificationServiceImplBase {

    @Autowired
    private NotificationRepository repository;

    // Streams actifs pour chaque utilisateur
    private final Map<String, StreamObserver<Notification>> activeStreams = new ConcurrentHashMap<>();
    
    // Executor pour les notifications périodiques
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // ==================== MODE 1: UNARY ====================
    // Utilisation: Envoi simple de notification (confirmation inscription, note publiée)
    
    @Override
    public void sendNotification(SendNotificationRequest request, 
                                 StreamObserver<SendNotificationResponse> responseObserver) {
        
        System.out.println("📨 [UNARY] Notification reçue pour: " + request.getUserId());
        
        // Sauvegarder en base
        NotificationEntity entity = new NotificationEntity();
        entity.setUserId(request.getUserId());
        entity.setTitle(request.getTitle());
        entity.setMessage(request.getMessage());
        entity.setType(request.getType().name());
        entity.setPriority(request.getPriority().name());
        entity.setTimestamp(LocalDateTime.now());
        entity = repository.save(entity);

        // Envoyer en temps réel si l'utilisateur est connecté
        if (activeStreams.containsKey(request.getUserId())) {
            try {
                activeStreams.get(request.getUserId()).onNext(mapToProto(entity));
                System.out.println("✅ Notification temps réel envoyée");
            } catch (Exception e) {
                activeStreams.remove(request.getUserId());
            }
        }

        // Réponse
        SendNotificationResponse response = SendNotificationResponse.newBuilder()
                .setNotificationId(String.valueOf(entity.getId()))
                .setSuccess(true)
                .setMessage("Notification enregistrée et envoyée")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // ==================== MODE 2: SERVER STREAMING ====================
    // Utilisation: Flux de notifications en temps réel (dashboard étudiant/prof)
    
    @Override
    public void subscribeToNotifications(SubscribeRequest request, 
                                         StreamObserver<Notification> responseObserver) {
        
        String userId = request.getUserId();
        System.out.println("🔔 [SERVER STREAMING] Abonnement: " + userId);

        // Enregistrer le stream
        activeStreams.put(userId, responseObserver);

        // Envoyer les notifications non lues
        repository.findByUserIdAndIsReadFalse(userId).forEach(entity -> {
            try {
                responseObserver.onNext(mapToProto(entity));
            } catch (Exception e) {
                System.err.println("Erreur envoi notification: " + e.getMessage());
            }
        });

        // Simuler des notifications périodiques (ex: rappels)
        scheduler.scheduleAtFixedRate(() -> {
            if (activeStreams.containsKey(userId)) {
                try {
                    Notification reminder = Notification.newBuilder()
                        .setId("reminder-" + System.currentTimeMillis())
                        .setUserId(userId)
                        .setTitle("Rappel Automatique")
                        .setMessage("N'oubliez pas de consulter vos cours aujourd'hui!")
                        .setType(NotificationType.GENERAL_INFO)
                        .setPriority(Priority.LOW)
                        .setTimestamp(LocalDateTime.now().toString())
                        .build();
                    
                    responseObserver.onNext(reminder);
                } catch (Exception e) {
                    activeStreams.remove(userId);
                }
            }
        }, 30, 60, TimeUnit.SECONDS); // Rappel toutes les 60s après 30s

        System.out.println("✅ Stream actif pour " + userId);
    }

    // ==================== MODE 3: CLIENT STREAMING ====================
    // Utilisation: Import massif de notifications (admin envoie à tous les étudiants)
    
    @Override
    public StreamObserver<SendNotificationRequest> sendBatchNotifications(
            StreamObserver<BatchNotificationResponse> responseObserver) {
        
        System.out.println("📦 [CLIENT STREAMING] Réception de lot...");
        
        return new StreamObserver<SendNotificationRequest>() {
            int successCount = 0;
            int totalCount = 0;

            @Override
            public void onNext(SendNotificationRequest request) {
                totalCount++;
                try {
                    // Sauvegarder
                    NotificationEntity entity = new NotificationEntity();
                    entity.setUserId(request.getUserId());
                    entity.setTitle(request.getTitle());
                    entity.setMessage(request.getMessage());
                    entity.setType(request.getType().name());
                    entity.setPriority(request.getPriority().name());
                    entity.setTimestamp(LocalDateTime.now());
                    repository.save(entity);
                    
                    // Envoyer en temps réel si connecté
                    if (activeStreams.containsKey(request.getUserId())) {
                        activeStreams.get(request.getUserId()).onNext(mapToProto(entity));
                    }
                    
                    successCount++;
                    System.out.println("✅ Notification " + totalCount + " traitée");
                } catch (Exception e) {
                    System.err.println("❌ Erreur notification " + totalCount + ": " + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("❌ Erreur batch: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                BatchNotificationResponse response = BatchNotificationResponse.newBuilder()
                        .setCountSuccess(successCount)
                        .setStatus("✅ " + successCount + "/" + totalCount + " notifications envoyées")
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
                System.out.println("📦 [CLIENT STREAMING] Lot terminé: " + successCount + "/" + totalCount);
            }
        };
    }

    // ==================== MODE 4: BIDIRECTIONAL STREAMING ====================
    // Utilisation: Chat en temps réel, synchronisation multi-appareils
    
    @Override
    public StreamObserver<AckRequest> syncNotifications(
            StreamObserver<AckResponse> responseObserver) {
        
        System.out.println("🔄 [BIDIRECTIONAL] Synchronisation démarrée");
        
        return new StreamObserver<AckRequest>() {
            @Override
            public void onNext(AckRequest request) {
                try {
                    // Marquer comme lu
                    Long id = Long.parseLong(request.getNotificationId());
                    repository.findById(id).ifPresent(n -> {
                        n.setRead(true);
                        repository.save(n);
                        System.out.println("✅ Notification " + id + " marquée comme lue");
                    });

                    // Compter les non lues
                    long unreadCount = repository.countByUserIdAndIsReadFalse(request.getUserId());
                    
                    // Répondre immédiatement
                    AckResponse response = AckResponse.newBuilder()
                            .setStatus("UPDATED")
                            .setUnreadCount((int) unreadCount)
                            .build();
                    
                    responseObserver.onNext(response);
                    
                    // Envoyer une nouvelle notification si disponible
                    repository.findByUserIdAndIsReadFalse(request.getUserId())
                        .stream()
                        .findFirst()
                        .ifPresent(nextNotif -> {
                            // Pas d'envoi direct ici, juste mise à jour du compteur
                            System.out.println("📬 " + unreadCount + " notifications restantes");
                        });
                    
                } catch (Exception e) {
                    System.err.println("❌ Erreur sync: " + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("❌ Erreur stream bidirectionnel: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
                System.out.println("🔄 [BIDIRECTIONAL] Synchronisation terminée");
            }
        };
    }

    // ==================== UTILITAIRES ====================

    private Notification mapToProto(NotificationEntity entity) {
        return Notification.newBuilder()
                .setId(String.valueOf(entity.getId()))
                .setUserId(entity.getUserId())
                .setTitle(entity.getTitle() != null ? entity.getTitle() : "")
                .setMessage(entity.getMessage() != null ? entity.getMessage() : "")
                .setType(NotificationType.valueOf(entity.getType()))
                .setPriority(Priority.valueOf(entity.getPriority()))
                .setRead(entity.isRead())
                .setTimestamp(entity.getTimestamp() != null ? entity.getTimestamp().toString() : "")
                .build();
    }

    // Nettoyage des streams inactifs
    public void cleanupInactiveStreams() {
        scheduler.scheduleAtFixedRate(() -> {
            activeStreams.entrySet().removeIf(entry -> {
                try {
                    // Test de connexion
                    entry.getValue().onNext(Notification.newBuilder()
                        .setId("ping")
                        .setUserId(entry.getKey())
                        .setTitle("Ping")
                        .setMessage("Test connexion")
                        .setType(NotificationType.GENERAL_INFO)
                        .setPriority(Priority.LOW)
                        .build());
                    return false;
                } catch (Exception e) {
                    System.out.println("🗑️ Stream inactif supprimé: " + entry.getKey());
                    return true;
                }
            });
        }, 5, 5, TimeUnit.MINUTES);
    }
}