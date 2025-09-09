package com.example.HNR.Events;

import com.example.HNR.Model.Common.NotificationType;
import com.example.HNR.Service.NotificationAudienceResolver;
import com.example.HNR.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationAudienceResolver audienceResolver;

    @EventListener
    @Async
    public void handleMissionCreatedEvent(MissionCreatedEvent event) {
        try {
            log.info("Handling MissionCreatedEvent for mission ID: {}", event.getMissionId());

            // Résoudre l'audience
            List<String> recipientIds = audienceResolver.resolveMissionCreatedAudience(
                    event.getMissionId(),
                    event.getCreatorUserId(),
                    event.getAssignedUserIds(),
                    event.getPrefecture(),
                    event.getCommune()
            );

            if (recipientIds.isEmpty()) {
                log.warn("No recipients found for mission created event: {}", event.getMissionId());
                return;
            }

            // Créer les données contextuelles
            Map<String, Object> data = new HashMap<>();
            data.put("missionId", event.getMissionId());
            data.put("missionType", event.getType());
            data.put("prefecture", event.getPrefecture());
            data.put("commune", event.getCommune());
            data.put("creatorUserId", event.getCreatorUserId());

            // Créer le titre et message
            String title = "Nouvelle mission créée";
            String message = String.format("Une nouvelle mission '%s' a été créée dans %s%s",
                    event.getMissionTitle(),
                    event.getPrefecture(),
                    event.getCommune() != null ? " - " + event.getCommune() : ""
            );

            // Créer les notifications
            notificationService.createMany(
                    recipientIds,
                    event.getCreatorUserId(),
                    NotificationType.MISSION_CREATED,
                    title,
                    message,
                    data
            );

            log.info("Created {} notifications for mission created event", recipientIds.size());

        } catch (Exception e) {
            log.error("Error handling MissionCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @EventListener
    @Async
    public void handleActionCreatedEvent(ActionCreatedEvent event) {
        try {
            log.info("Handling ActionCreatedEvent for action ID: {}", event.getActionId());

            // Résoudre l'audience
            List<String> recipientIds = audienceResolver.resolveActionCreatedAudience(
                    event.getActionId(),
                    event.getActionUserId(),
                    event.getPrefecture(),
                    event.getCommune()
            );

            if (recipientIds.isEmpty()) {
                log.warn("No recipients found for action created event: {}", event.getActionId());
                return;
            }

            // Créer les données contextuelles
            Map<String, Object> data = new HashMap<>();
            data.put("actionId", event.getActionId());
            data.put("actionType", event.getActionType().toString());
            data.put("prefecture", event.getPrefecture());
            data.put("commune", event.getCommune());
            data.put("douarId", event.getDouarId());
            data.put("missionId", event.getMissionId());

            // Créer le titre et message
            String title = "Nouvelle action créée";
            String message = String.format("Une nouvelle action de type %s a été créée dans %s%s",
                    event.getActionType().toString(),
                    event.getPrefecture(),
                    event.getCommune() != null ? " - " + event.getCommune() : ""
            );

            // Créer les notifications
            notificationService.createMany(
                    recipientIds,
                    event.getActionUserId(),
                    NotificationType.ACTION_CREATED,
                    title,
                    message,
                    data
            );

            log.info("Created {} notifications for action created event", recipientIds.size());

        } catch (Exception e) {
            log.error("Error handling ActionCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @EventListener
    @Async
    public void handleChangementDeclaredEvent(ChangementDeclaredEvent event) {
        try {
            log.info("Handling ChangementDeclaredEvent for changement ID: {}", event.getChangementId());

            // Résoudre l'audience
            List<String> recipientIds = audienceResolver.resolveChangementDeclaredAudience(
                    event.getChangementId(),
                    event.getDetectedByUserId(),
                    event.getPrefecture(),
                    event.getCommune()
            );

            if (recipientIds.isEmpty()) {
                log.warn("No recipients found for changement declared event: {}", event.getChangementId());
                return;
            }

            // Créer les données contextuelles
            Map<String, Object> data = new HashMap<>();
            data.put("changementId", event.getChangementId());
            data.put("extensionType", event.getExtensionType().toString());
            data.put("prefecture", event.getPrefecture());
            data.put("commune", event.getCommune());
            data.put("douarId", event.getDouarId());

            // Créer le titre et message
            String title = "Nouveau changement déclaré";
            String message = String.format("Un changement de type %s (%.2f m²) a été détecté dans %s%s",
                    event.getExtensionType().toString(),
                    event.getPrefecture(),
                    event.getCommune() != null ? " - " + event.getCommune() : ""
            );

            // Créer les notifications
            notificationService.createMany(
                    recipientIds,
                    event.getDetectedByUserId(),
                    NotificationType.CHANGEMENT_DECLARED,
                    title,
                    message,
                    data
            );

            log.info("Created {} notifications for changement declared event", recipientIds.size());

        } catch (Exception e) {
            log.error("Error handling ChangementDeclaredEvent: {}", e.getMessage(), e);
        }
    }

    // Événement pour la création d'un utilisateur
    @EventListener
    @Async
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        try {
            log.info("Handling UserCreatedEvent for user: {}", event.getNewUserId());

            // Résoudre l'audience
            List<String> recipientIds = audienceResolver.resolveUserCreatedAudience(
                    event.getNewUserId(),
                    event.getCreatorUserId(),
                    event.getNewUserRole(),
                    event.getPrefecture()
            );

            if (recipientIds.isEmpty()) {
                log.warn("No recipients found for user created event: {}", event.getNewUserId());
                return;
            }

            // Créer les données contextuelles
            Map<String, Object> data = new HashMap<>();
            data.put("newUserId", event.getNewUserId());
            data.put("newUserName", event.getNewUserName());
            data.put("newUserRole", event.getNewUserRole().toString());
            data.put("prefecture", event.getPrefecture());
            data.put("commune", event.getCommune());

            // Créer le titre et message
            String title = "Nouvel utilisateur créé";
            String message = String.format("Un nouvel utilisateur %s (%s) a été créé pour %s",
                    event.getNewUserName(),
                    event.getNewUserRole().toString(),
                    event.getPrefecture()
            );

            // Créer les notifications
            notificationService.createMany(
                    recipientIds,
                    event.getCreatorUserId(),
                    NotificationType.USER_CREATED,
                    title,
                    message,
                    data
            );

            log.info("Created {} notifications for user created event", recipientIds.size());

        } catch (Exception e) {
            log.error("Error handling UserCreatedEvent: {}", e.getMessage(), e);
        }
    }
}