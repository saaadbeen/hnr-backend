package com.example.HNR.Service;

import com.example.HNR.Model.Common.NotificationType;
import com.example.HNR.Model.Mongodb.Notification;
import com.example.HNR.Repository.Mongodb.NotificationRepository;
import com.example.HNR.Service.NotificationStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    // Cette interface sera injectée automatiquement quand on créera le service SSE
    private final NotificationStreamService notificationStreamService;
    @Override
    public Notification create(String recipientUserId, NotificationType type, String title, String message) {
        return create(recipientUserId, null, type, title, message, null);
    }

    @Override
    public Notification create(String recipientUserId, String senderUserId, NotificationType type,
                               String title, String message, Map<String, Object> data) {

        log.debug("Creating notification for user {} with type {}", recipientUserId, type);

        Notification notification = Notification.builder()
                .recipientUserId(recipientUserId)
                .senderUserId(senderUserId)
                .type(type)
                .title(title)
                .message(message)
                .data(data)
                .isRead(false)
                .createdAt(new Date())
                .build();

        Notification saved = notificationRepository.save(notification);

        // Déclencher le push temps-réel (SSE)
        if (notificationStreamService != null) {
            try {
                notificationStreamService.pushNotification(recipientUserId, saved);
            } catch (Exception e) {
                log.warn("Failed to send real-time notification to user {}: {}", recipientUserId, e.getMessage());
            }
        }

        log.info("Notification created with ID {} for user {}", saved.getId(), recipientUserId);
        return saved;
    }

    @Override
    public List<Notification> createMany(List<String> recipientUserIds, NotificationType type,
                                         String title, String message) {
        return createMany(recipientUserIds, null, type, title, message, null);
    }

    @Override
    public List<Notification> createMany(List<String> recipientUserIds, String senderUserId,
                                         NotificationType type, String title, String message,
                                         Map<String, Object> data) {

        log.debug("Creating {} notifications of type {}", recipientUserIds.size(), type);

        List<Notification> notifications = recipientUserIds.stream()
                .map(recipientId -> Notification.builder()
                        .recipientUserId(recipientId)
                        .senderUserId(senderUserId)
                        .type(type)
                        .title(title)
                        .message(message)
                        .data(data)
                        .isRead(false)
                        .createdAt(new Date())
                        .build())
                .collect(Collectors.toList());

        List<Notification> saved = notificationRepository.saveAll(notifications);

        // Push temps-réel pour chaque utilisateur
        if (notificationStreamService != null) {
            saved.forEach(notification -> {
                try {
                    notificationStreamService.pushNotification(notification.getRecipientUserId(), notification);
                } catch (Exception e) {
                    log.warn("Failed to send real-time notification to user {}: {}",
                            notification.getRecipientUserId(), e.getMessage());
                }
            });
        }

        log.info("Created {} notifications successfully", saved.size());
        return saved;
    }

    @Override
    public List<Notification> listForUser(String userId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Page<Notification> listForUser(String userId, Pageable pageable) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional
    public Optional<Notification> markAsRead(String notificationId, String userId) {
        Optional<Notification> optNotification = notificationRepository.findById(notificationId);

        if (optNotification.isEmpty()) {
            log.warn("Notification with ID {} not found", notificationId);
            return Optional.empty();
        }

        Notification notification = optNotification.get();

        // Vérifier que l'utilisateur est bien le destinataire
        if (!notification.getRecipientUserId().equals(userId)) {
            log.warn("User {} attempted to mark notification {} as read, but is not the recipient",
                    userId, notificationId);
            return Optional.empty();
        }

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification = notificationRepository.save(notification);
            log.debug("Marked notification {} as read for user {}", notificationId, userId);
        }

        return Optional.of(notification);
    }

    @Override
    @Transactional
    public long markAllAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        if (unreadNotifications.isEmpty()) {
            return 0;
        }

        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);

        log.info("Marked {} notifications as read for user {}", unreadNotifications.size(), userId);
        return unreadNotifications.size();
    }

    @Override
    @Transactional
    public boolean delete(String notificationId, String userId) {
        Optional<Notification> optNotification = notificationRepository.findById(notificationId);

        if (optNotification.isEmpty()) {
            log.warn("Notification with ID {} not found for deletion", notificationId);
            return false;
        }

        Notification notification = optNotification.get();

        // Vérifier que l'utilisateur est bien le destinataire
        if (!notification.getRecipientUserId().equals(userId)) {
            log.warn("User {} attempted to delete notification {}, but is not the recipient",
                    userId, notificationId);
            return false;
        }

        notificationRepository.delete(notification);
        log.info("Deleted notification {} for user {}", notificationId, userId);
        return true;
    }

    @Override
    public long unreadCount(String userId) {
        return notificationRepository.countByRecipientUserIdAndIsReadFalse(userId);
    }

    @Override
    public Optional<Notification> findById(String notificationId) {
        return notificationRepository.findById(notificationId);
    }

    @Override
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public long cleanupOldNotifications(int daysOld) {
        Date cutoffDate = Date.from(
                LocalDateTime.now().minusDays(daysOld).atZone(ZoneId.systemDefault()).toInstant()
        );

        long countBefore = notificationRepository.count();
        notificationRepository.deleteByCreatedAtBefore(cutoffDate);
        long countAfter = notificationRepository.count();

        long deletedCount = countBefore - countAfter;
        log.info("Cleaned up {} old notifications (older than {} days)", deletedCount, daysOld);

        return deletedCount;
    }

    @Override
    public Map<String, Object> getNotificationStats(String userId) {
        Map<String, Object> stats = new HashMap<>();

        long totalCount = notificationRepository.countByRecipientUserId(userId);
        long unreadCount = notificationRepository.countByRecipientUserIdAndIsReadFalse(userId);

        stats.put("totalCount", totalCount);
        stats.put("unreadCount", unreadCount);
        stats.put("readCount", totalCount - unreadCount);
        stats.put("readPercentage", totalCount > 0 ? (double) (totalCount - unreadCount) / totalCount * 100 : 0);

        return stats;
    }


}