package com.example.HNR.Controller;

import com.example.HNR.Config.JwtTokenUtil;
import com.example.HNR.Service.NotificationStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationStreamController {

    private final NotificationStreamService notificationStreamService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<?> streamNotifications(
            @RequestParam(required = false) String token,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request) {

        Map<String, Object> error = new HashMap<>();

        try {
            // 1) Extraire le JWT
            String jwt = null;
            if (token != null && !token.isEmpty()) {
                jwt = token;
            } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }
            if (jwt == null || jwt.isEmpty()) {
                error.put("error", "Token JWT manquant");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // 2) Valider
            if (!jwtTokenUtil.validateToken(jwt)) {
                error.put("error", "Token JWT invalide ou expiré");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // 3) userId depuis le token (recommandé)
            String userId = jwtTokenUtil.getUserIdFromToken(jwt);

            // (facultatif) fallback email -> userId si nécessaire
            if (userId == null || userId.isEmpty()) {
                String email = jwtTokenUtil.getUsernameFromToken(jwt);
                // TODO: si tu as un userService: userId = userService.findIdByEmail(email);
            }
            if (userId == null || userId.isEmpty()) {
                error.put("error", "Impossible de déterminer l'utilisateur (userId manquant)");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // 4) Ouvrir la connexion SSE
            SseEmitter emitter = notificationStreamService.createConnection(userId);

            // Pas d'entêtes CORS ici (gérées globalement). Anti-buffering ok:
            return ResponseEntity.ok()
                    .header("Cache-Control", "no-cache")
                    .header("X-Accel-Buffering", "no")
                    .body(emitter);

        } catch (Exception e) {
            log.error("Error establishing SSE connection", e);
            error.put("error", "Erreur lors de l'établissement de la connexion temps réel");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/stream/status")
    @PreAuthorize("hasRole('MEMBRE_DSI')")
    public ResponseEntity<Map<String, Object>> getStreamStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            status.put("activeConnections", notificationStreamService.getActiveConnectionsCount());
            status.put("connectionsByUser", notificationStreamService.getConnectionsByUser());
            status.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error getting stream status: {}", e.getMessage());
            status.put("error", "Erreur lors de la récupération du statut");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    @PostMapping("/stream/disconnect/{userId}")
    @PreAuthorize("hasRole('MEMBRE_DSI')")
    public ResponseEntity<Map<String, Object>> disconnectUser(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int disconnected = notificationStreamService.disconnectUser(userId);
            response.put("status", "success");
            response.put("message", "Connexions fermées pour l'utilisateur");
            response.put("disconnectedConnections", disconnected);
            log.info("Admin disconnected {} connections for user: {}", disconnected, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error disconnecting user {}: {}", userId, e.getMessage());
            response.put("status", "error");
            response.put("message", "Erreur lors de la déconnexion");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/stream/test")
    @PreAuthorize("hasRole('MEMBRE_DSI')")
    public ResponseEntity<Map<String, Object>> sendTestNotification(
            @RequestParam String userId,
            @RequestParam(defaultValue = "Test de notification") String message) {

        Map<String, Object> response = new HashMap<>();
        try {
            boolean sent = notificationStreamService.sendTestNotification(userId, message);
            response.put("status", sent ? "success" : "warning");
            response.put("message", sent ? "Notification de test envoyée" : "Aucune connexion active pour cet utilisateur");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending test notification to {}: {}", userId, e.getMessage());
            response.put("status", "error");
            response.put("message", "Erreur lors de l'envoi de la notification de test");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/stream/cleanup")
    @PreAuthorize("hasRole('MEMBRE_DSI')")
    public ResponseEntity<Map<String, Object>> cleanupConnections() {
        Map<String, Object> response = new HashMap<>();
        try {
            int cleaned = notificationStreamService.cleanupClosedConnections();
            response.put("status", "success");
            response.put("message", "Nettoyage des connexions effectué");
            response.put("cleanedConnections", cleaned);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error cleaning up connections: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "Erreur lors du nettoyage");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
