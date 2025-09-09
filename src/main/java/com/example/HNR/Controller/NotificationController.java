package com.example.HNR.Controller;

import com.example.HNR.Model.Mongodb.Notification;
import com.example.HNR.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Obtenir toutes les notifications de l'utilisateur connecté
     */
    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    @CrossOrigin(
              origins = {"http://localhost:3000","http://127.0.0.1:3000"},
              allowedHeaders = {"Authorization","Content-Type","Cache-Control"},
              methods = {RequestMethod.GET, RequestMethod.OPTIONS},
              allowCredentials = "false", maxAge = 3600)
    public ResponseEntity<Map<String, Object>> getMyNotifications(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String userId = getUserId(auth);
        Map<String, Object> response = new HashMap<>();

        try {
            if (size > 0) {
                // Version paginée
                Pageable pageable = PageRequest.of(page, size);
                Page<Notification> notificationsPage = notificationService.listForUser(userId, pageable);

                response.put("notifications", notificationsPage.getContent());
                response.put("totalElements", notificationsPage.getTotalElements());
                response.put("totalPages", notificationsPage.getTotalPages());
                response.put("currentPage", page);
                response.put("size", size);
            } else {
                // Toutes les notifications
                List<Notification> notifications = notificationService.listForUser(userId);
                response.put("notifications", notifications);
                response.put("totalElements", notifications.size());
            }

            // Ajouter le nombre de non lues
            long unreadCount = notificationService.unreadCount(userId);
            response.put("unreadCount", unreadCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Erreur lors de la récupération des notifications");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtenir le nombre de notifications non lues
     */
    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Map<String, Object>> getUnreadCount(Authentication auth) {
        String userId = getUserId(auth);
        Map<String, Object> response = new HashMap<>();

        try {
            long unreadCount = notificationService.unreadCount(userId);
            response.put("unreadCount", unreadCount);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Erreur lors du comptage des notifications non lues");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtenir les notifications non lues uniquement
     */
    @GetMapping("/unread")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications(Authentication auth) {
        String userId = getUserId(auth);
        Map<String, Object> response = new HashMap<>();

        try {
            List<Notification> unreadNotifications = notificationService.getUnreadNotifications(userId);
            response.put("notifications", unreadNotifications);
            response.put("count", unreadNotifications.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Erreur lors de la récupération des notifications non lues");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Marquer une notification comme lue
     */
    @PostMapping("/{id}/read")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable String id,
            Authentication auth) {

        String userId = getUserId(auth);
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Notification> notification = notificationService.markAsRead(id, userId);

            if (notification.isPresent()) {
                response.put("status", "success");
                response.put("message", "Notification marquée comme lue");
                response.put("notification", notification.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Notification non trouvée ou non autorisée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la mise à jour de la notification");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    @PostMapping("/read-all")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Map<String, Object>> markAllAsRead(Authentication auth) {
        String userId = getUserId(auth);
        Map<String, Object> response = new HashMap<>();

        try {
            long markedCount = notificationService.markAllAsRead(userId);

            response.put("status", "success");
            response.put("message", "Toutes les notifications ont été marquées comme lues");
            response.put("markedCount", markedCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la mise à jour des notifications");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Supprimer une notification
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @PathVariable String id,
            Authentication auth) {

        String userId = getUserId(auth);
        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = notificationService.delete(id, userId);

            if (deleted) {
                response.put("status", "success");
                response.put("message", "Notification supprimée avec succès");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Notification non trouvée ou non autorisée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la suppression de la notification");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtenir les statistiques des notifications
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Map<String, Object>> getNotificationStats(Authentication auth) {
        String userId = getUserId(auth);

        try {
            Map<String, Object> stats = notificationService.getNotificationStats(userId);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Erreur lors de la récupération des statistiques");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtenir une notification spécifique (pour les détails)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Map<String, Object>> getNotificationById(
            @PathVariable String id,
            Authentication auth) {

        String userId = getUserId(auth);
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Notification> notification = notificationService.findById(id);

            if (notification.isPresent()) {
                Notification notif = notification.get();

                // Vérifier que l'utilisateur est le destinataire
                if (!notif.getRecipientUserId().equals(userId)) {
                    response.put("status", "error");
                    response.put("message", "Non autorisé à voir cette notification");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }

                response.put("notification", notif);
                return ResponseEntity.ok(response);

            } else {
                response.put("status", "error");
                response.put("message", "Notification non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération de la notification");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Méthode utilitaire pour extraire l'ID utilisateur depuis l'authentification
    private String getUserId(Authentication auth) {
        // Selon ta configuration JWT, tu peux avoir besoin d'adapter cette méthode
        return auth.getName(); // Généralement l'email dans ton système
    }
}