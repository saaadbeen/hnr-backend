package com.example.HNR.Service;

import com.example.HNR.Model.Mongodb.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class NotificationStreamService {

    private final ObjectMapper objectMapper;

    // Timeout pour les connexions SSE (30 minutes)
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    // Map des connexions actives : userId -> List<SseEmitter>
    private final Map<String, List<SseEmitter>> activeConnections = new ConcurrentHashMap<>();

    // Executor pour les tâches de nettoyage périodique
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Initialisation du service avec nettoyage périodique
    public NotificationStreamService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        // Nettoyer les connexions fermées toutes les 5 minutes
        scheduler.scheduleAtFixedRate(this::cleanupClosedConnections, 5, 5, TimeUnit.MINUTES);

        log.info("NotificationStreamService initialized");
    }

    /**
     * Créer une nouvelle connexion SSE pour un utilisateur
     */
    public SseEmitter createConnection(String userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // Callbacks
        emitter.onCompletion(() -> {
            log.debug("SSE connection completed for user: {}", userId);
            removeConnection(userId, emitter);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE connection timed out for user: {}", userId);
            removeConnection(userId, emitter);
        });

        emitter.onError((ex) -> {
            log.debug("SSE connection error for user {}: {}", userId, ex.getMessage());
            removeConnection(userId, emitter);
        });

        // Ajouter la connexion à la map
        activeConnections.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // Envoyer un message de connexion réussie
        try {
            Map<String, Object> connectionMessage = new HashMap<>();
            connectionMessage.put("type", "connection");
            connectionMessage.put("message", "Connexion aux notifications établie");
            connectionMessage.put("timestamp", System.currentTimeMillis());

            emitter.send(SseEmitter.event()
                    .name("connection")
                    .data(objectMapper.writeValueAsString(connectionMessage)));

        } catch (IOException e) {
            log.error("Error sending connection message to {}: {}", userId, e.getMessage());
            removeConnection(userId, emitter);
        }

        log.info("SSE connection established for user: {}. Total connections: {}",
                userId, getTotalConnectionsCount());

        return emitter;
    }

    /**
     * Pousser une notification vers un utilisateur spécifique
     */
    public void pushNotification(String userId, Notification notification) {
        List<SseEmitter> userConnections = activeConnections.get(userId);

        if (userConnections == null || userConnections.isEmpty()) {
            log.debug("No active connections for user: {}", userId);
            return;
        }

        try {
            // Préparer les données de la notification
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("type", "notification");
            notificationData.put("data", notification);
            notificationData.put("timestamp", System.currentTimeMillis());

            String jsonData = objectMapper.writeValueAsString(notificationData);

            // Envoyer et retirer les émetteurs cassés
            userConnections.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(jsonData));
                    return false; // garder
                } catch (IOException e) {
                    log.warn("Failed to send notification to connection for user {}: {}",
                            userId, e.getMessage());
                    try { emitter.completeWithError(e); } catch (Exception ignored) {}
                    return true; // retirer
                }
            });

            log.debug("Notification pushed to {} connections for user: {}",
                    userConnections.size(), userId);

        } catch (Exception e) {
            log.error("Error pushing notification to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Envoyer une notification de test (pour debug)
     */
    public boolean sendTestNotification(String userId, String message) {
        List<SseEmitter> userConnections = activeConnections.get(userId);

        if (userConnections == null || userConnections.isEmpty()) {
            return false;
        }

        try {
            Map<String, Object> testData = new HashMap<>();
            testData.put("type", "test");
            testData.put("message", message);
            testData.put("timestamp", System.currentTimeMillis());

            String jsonData = objectMapper.writeValueAsString(testData);

            userConnections.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("test")
                            .data(jsonData));
                    return false;
                } catch (IOException e) {
                    log.warn("Failed to send test notification: {}", e.getMessage());
                    try { emitter.completeWithError(e); } catch (Exception ignored) {}
                    return true;
                }
            });

            return true;

        } catch (Exception e) {
            log.error("Error sending test notification: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Déconnecter toutes les connexions d'un utilisateur
     */
    public int disconnectUser(String userId) {
        List<SseEmitter> userConnections = activeConnections.remove(userId);

        if (userConnections == null || userConnections.isEmpty()) {
            return 0;
        }

        int disconnectedCount = 0;
        for (SseEmitter emitter : userConnections) {
            try {
                emitter.complete();
                disconnectedCount++;
            } catch (Exception e) {
                log.warn("Error completing emitter for user {}: {}", userId, e.getMessage());
            }
        }

        log.info("Disconnected {} connections for user: {}", disconnectedCount, userId);
        return disconnectedCount;
    }

    /**
     * Nettoyer les connexions fermées ou en erreur
     */
    public int cleanupClosedConnections() {
        int cleanedGroups = 0;

        for (Map.Entry<String, List<SseEmitter>> entry : activeConnections.entrySet()) {
            String userId = entry.getKey();
            List<SseEmitter> connections = entry.getValue();

            boolean removedAny = connections.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event().name("ping").data("ping"));
                    return false; // OK
                } catch (Exception e) {
                    // Connexion fermée ou en erreur
                    try { emitter.complete(); } catch (Exception ignored) {}
                    return true; // retirer
                }
            });

            if (removedAny) cleanedGroups++;
            if (connections.isEmpty()) {
                activeConnections.remove(userId);
            }
        }

        if (cleanedGroups > 0) {
            log.info("Cleaned up {} closed connection groups", cleanedGroups);
        }

        return cleanedGroups;
    }

    /**
     * Obtenir le nombre total de connexions actives
     */
    public int getActiveConnectionsCount() {
        return activeConnections.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    /**
     * Obtenir le nombre de connexions par utilisateur
     */
    public Map<String, Integer> getConnectionsByUser() {
        Map<String, Integer> result = new HashMap<>();
        activeConnections.forEach((userId, connections) -> {
            result.put(userId, connections.size());
        });
        return result;
    }

    /**
     * Envoyer un message de diffusion à tous les utilisateurs connectés (admin)
     */
    public int broadcastMessage(String title, String message) {
        AtomicInteger sentCount = new AtomicInteger(0);

        try {
            Map<String, Object> broadcastData = new HashMap<>();
            broadcastData.put("type", "broadcast");
            broadcastData.put("title", title);
            broadcastData.put("message", message);
            broadcastData.put("timestamp", System.currentTimeMillis());

            String jsonData = objectMapper.writeValueAsString(broadcastData);

            activeConnections.forEach((userId, connections) -> {
                connections.removeIf(emitter -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("broadcast")
                                .data(jsonData));
                        sentCount.incrementAndGet();
                        return false;
                    } catch (IOException e) {
                        log.warn("Failed to send broadcast to user {}: {}", userId, e.getMessage());
                        try { emitter.completeWithError(e); } catch (Exception ignored) {}
                        return true;
                    }
                });
            });

            log.info("Broadcast message sent to {} connections", sentCount.get());

        } catch (Exception e) {
            log.error("Error sending broadcast message: {}", e.getMessage());
        }

        return sentCount.get();
    }

    // Méthodes privées

    private void removeConnection(String userId, SseEmitter emitter) {
        List<SseEmitter> userConnections = activeConnections.get(userId);
        if (userConnections != null) {
            userConnections.remove(emitter);
            if (userConnections.isEmpty()) {
                activeConnections.remove(userId);
            }
        }
    }

    private int getTotalConnectionsCount() {
        return activeConnections.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down NotificationStreamService...");

        // Fermer toutes les connexions
        activeConnections.forEach((userId, connections) -> {
            connections.forEach(emitter -> {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    log.warn("Error completing emitter during shutdown: {}", e.getMessage());
                }
            });
        });

        activeConnections.clear();

        // Arrêter le scheduler
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("NotificationStreamService shut down completed");
    }
}
