package com.example.HNR.Repository.Mongodb;

import com.example.HNR.Model.Common.NotificationType;
import com.example.HNR.Model.Mongodb.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    // Trouver toutes les notifications d'un utilisateur, triées par date (plus récente en premier)
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(String recipientUserId);

    // Version paginée
    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(String recipientUserId, Pageable pageable);

    // Compter les notifications non lues d'un utilisateur
    long countByRecipientUserIdAndIsReadFalse(String recipientUserId);

    // Trouver les notifications non lues d'un utilisateur
    List<Notification> findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(String recipientUserId);

    // Trouver par type de notification
    List<Notification> findByRecipientUserIdAndTypeOrderByCreatedAtDesc(String recipientUserId, NotificationType type);

    // Trouver par émetteur
    List<Notification> findBySenderUserIdOrderByCreatedAtDesc(String senderUserId);

    // Trouver les notifications par plage de dates
    List<Notification> findByRecipientUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            String recipientUserId, Date startDate, Date endDate);

    // Supprimer les anciennes notifications (pour nettoyage)
    void deleteByCreatedAtBefore(Date date);

    // Supprimer toutes les notifications d'un utilisateur
    void deleteByRecipientUserId(String recipientUserId);

    // Marquer toutes les notifications d'un utilisateur comme lues
    @Query("{ 'recipientUserId': ?0, 'isRead': false }")
    List<Notification> findUnreadNotificationsByRecipientUserId(String recipientUserId);

    // Statistiques : compter par type pour un utilisateur
    @Query(value = "{ 'recipientUserId': ?0 }", count = true)
    long countByRecipientUserId(String recipientUserId);

    // Dernières notifications globales (pour admin/debug)
    List<Notification> findTop10ByOrderByCreatedAtDesc();
}