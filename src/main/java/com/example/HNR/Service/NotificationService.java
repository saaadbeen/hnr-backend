package com.example.HNR.Service;

import com.example.HNR.Model.Common.NotificationType;
import com.example.HNR.Model.Mongodb.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NotificationService {

    /**
     * Créer une notification simple
     */
    Notification create(String recipientUserId, NotificationType type, String title, String message);

    /**
     * Créer une notification avec des données contextuelles
     */
    Notification create(String recipientUserId, String senderUserId, NotificationType type,
                        String title, String message, Map<String, Object> data);

    /**
     * Créer plusieurs notifications pour plusieurs destinataires
     */
    List<Notification> createMany(List<String> recipientUserIds, NotificationType type,
                                  String title, String message);

    /**
     * Créer plusieurs notifications avec des données
     */
    List<Notification> createMany(List<String> recipientUserIds, String senderUserId,
                                  NotificationType type, String title, String message,
                                  Map<String, Object> data);

    /**
     * Obtenir toutes les notifications d'un utilisateur
     */
    List<Notification> listForUser(String userId);

    /**
     * Obtenir les notifications d'un utilisateur avec pagination
     */
    Page<Notification> listForUser(String userId, Pageable pageable);

    /**
     * Marquer une notification comme lue
     */
    Optional<Notification> markAsRead(String notificationId, String userId);

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    long markAllAsRead(String userId);

    /**
     * Supprimer une notification
     */
    boolean delete(String notificationId, String userId);

    /**
     * Compter les notifications non lues d'un utilisateur
     */
    long unreadCount(String userId);

    /**
     * Obtenir une notification par ID
     */
    Optional<Notification> findById(String notificationId);

    /**
     * Obtenir les notifications non lues d'un utilisateur
     */
    List<Notification> getUnreadNotifications(String userId);

    /**
     * Nettoyer les anciennes notifications (tâche de maintenance)
     */
    long cleanupOldNotifications(int daysOld);

    /**
     * Obtenir les statistiques de notifications pour un utilisateur
     */
    Map<String, Object> getNotificationStats(String userId);
}