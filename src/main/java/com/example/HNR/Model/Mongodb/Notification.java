package com.example.HNR.Model.Mongodb;

import com.example.HNR.Model.Common.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;
import java.util.Map;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {

    @Id
    private String id;

    @Indexed
    @Builder.Default
    private String recipientUserId = "";  // utilisateur destinataire (MongoDB)

    private String senderUserId;          // utilisateur émetteur (MongoDB, optionnel)

    @Indexed
    @Builder.Default
    private NotificationType type = NotificationType.NOTIFICATION_GENERAL;

    @Builder.Default
    private String title = "";            // titre court de la notification

    @Builder.Default
    private String message = "";          // message détaillé

    private Map<String, Object> data;     // payload libre (contexte, IDs, URLs, etc.)

    @Builder.Default
    private Boolean isRead = false;       // statut de lecture

    @CreatedDate
    @Indexed
    @Builder.Default
    private Date createdAt = new Date();

    // Constructeur pour faciliter la création
    public Notification(String recipientUserId, NotificationType type, String title, String message) {
        this.recipientUserId = recipientUserId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = false;
        this.createdAt = new Date();
    }

    // Constructeur avec data
    public Notification(String recipientUserId, NotificationType type, String title, String message, Map<String, Object> data) {
        this(recipientUserId, type, title, message);
        this.data = data;
    }

    // Constructeur complet
    public Notification(String recipientUserId, String senderUserId, NotificationType type, String title, String message, Map<String, Object> data) {
        this(recipientUserId, type, title, message, data);
        this.senderUserId = senderUserId;
    }

    // Méthode utilitaire pour marquer comme lue
    public void markAsRead() {
        this.isRead = true;
    }

    // Méthode utilitaire pour vérifier si non lue
    public boolean isUnread() {
        return !Boolean.TRUE.equals(this.isRead);
    }
}