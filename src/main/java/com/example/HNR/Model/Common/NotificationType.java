package com.example.HNR.Model.Common;

public enum NotificationType {
    USER_CREATED("Nouvel utilisateur créé"),
    MISSION_CREATED("Nouvelle mission créée"),
    MISSION_ASSIGNED("Mission assignée"),
    MISSION_UPDATED("Mission mise à jour"),
    MISSION_COMPLETED("Mission terminée"),
    ACTION_CREATED("Nouvelle action créée"),
    ACTION_COMPLETED("Action terminée"),
    CHANGEMENT_DECLARED("Nouveau changement déclaré"),
    PV_GENERATED("PV généré"),
    SYSTEM_UPDATE("Mise à jour système"),
    NOTIFICATION_GENERAL("Notification générale");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}