package com.university.notification.service;

import com.university.notification.models.NotificationEntity;
import com.university.notification.repositories.NotificationRepository;
import com.university.notifications.stubs.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationServiceImpl extends NotificationServiceGrpc.NotificationServiceImplBase {

    @Autowired
    private NotificationRepository repository;

    private final Map<String, StreamObserver<Notification>> activeStreams = new ConcurrentHashMap<>();

    @Override
    public void sendNotification(SendNotificationRequest request, StreamObserver<SendNotificationResponse> responseObserver) {
        
        NotificationEntity entity = new NotificationEntity();
        entity.setUserId(request.getUserId());
        entity.setTitle(request.getTitle());
        entity.setMessage(request.getMessage());
        entity.setType(request.getType().name());
        entity.setPriority(request.getPriority().name());
        entity = repository.save(entity);

        if (activeStreams.containsKey(request.getUserId())) {
            StreamObserver<Notification> clientStream = activeStreams.get(request.getUserId());
            try {
                clientStream.onNext(mapToProto(entity));
            } catch (Exception e) {
                activeStreams.remove(request.getUserId()); 
            }
        }

        SendNotificationResponse response = SendNotificationResponse.newBuilder()
                .setNotificationId(String.valueOf(entity.getId()))
                .setSuccess(true)
                .setMessage("Notification enregistrée et envoyée.")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeToNotifications(SubscribeRequest request, StreamObserver<Notification> responseObserver) {
        String userId = request.getUserId();
        System.out.println("🔔 Utilisateur connecté au flux : " + userId);

        activeStreams.put(userId, responseObserver);

        repository.findByUserIdAndIsReadFalse(userId).forEach(entity -> {
            responseObserver.onNext(mapToProto(entity));
        });

       
    }

    @Override
    public StreamObserver<SendNotificationRequest> sendBatchNotifications(
            StreamObserver<BatchNotificationResponse> responseObserver) {
        
        return new StreamObserver<SendNotificationRequest>() {
            int successCount = 0;

            @Override
            public void onNext(SendNotificationRequest request) {
                NotificationEntity entity = new NotificationEntity();
                entity.setUserId(request.getUserId());
                entity.setTitle(request.getTitle());
                entity.setMessage(request.getMessage());
                entity.setType(request.getType().name());
                entity.setPriority(request.getPriority().name());
                repository.save(entity);
                successCount++;
                
                if (activeStreams.containsKey(request.getUserId())) {
                    activeStreams.get(request.getUserId()).onNext(mapToProto(entity));
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Erreur batch : " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(BatchNotificationResponse.newBuilder()
                        .setCountSuccess(successCount)
                        .setStatus("Batch traité avec succès")
                        .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<AckRequest> syncNotifications(
            StreamObserver<AckResponse> responseObserver) {
        
        return new StreamObserver<AckRequest>() {
            @Override
            public void onNext(AckRequest request) {
                try {
                    Long id = Long.parseLong(request.getNotificationId());
                    repository.findById(id).ifPresent(n -> {
                        n.setRead(true); 
                        repository.save(n);
                    });

                    long count = repository.countByUserIdAndIsReadFalse(request.getUserId());
                    responseObserver.onNext(AckResponse.newBuilder()
                            .setStatus("UPDATED")
                            .setUnreadCount((int) count)
                            .build());
                } catch (Exception e) {
                    System.err.println("Erreur sync: " + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Erreur dans le flux : " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    private Notification mapToProto(NotificationEntity entity) {
        return Notification.newBuilder()
                .setId(String.valueOf(entity.getId()))
                .setUserId(entity.getUserId())
                .setTitle(entity.getTitle() != null ? entity.getTitle() : "")
                .setMessage(entity.getMessage() != null ? entity.getMessage() : "")
                .setType(NotificationType.valueOf(entity.getType()))
                .setPriority(Priority.valueOf(entity.getPriority()))
                .setRead(entity.isRead())
                .setTimestamp(entity.getTimestamp().toString())
                .build();
    }
}