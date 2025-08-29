package com.example.HNR.Model.SqlServer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

/**
 * Entité Fichier stockée dans SQL Server
 * Gestion des documents et photos avec historique
 */
@Entity
@Table(name = "fichiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fichierId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private Long size; // Taille en octets

    @Column(name = "entity_type", length = 50)
    private String entityType; // Type d'entité liée

    @Column(name = "entity_id")
    private Long entityId; // ID de l'entité liée

    // Relations optionnelles
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changement_id")
    private Changement changement;

    // Référence vers l'utilisateur qui a uploadé (stocké dans MongoDB)
    @Column(name = "uploaded_by_user_id", nullable = false, length = 100)
    private String uploadedByUserId;

    // Métadonnées
    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Date uploadedAt;

    @Column(name = "deleted_at")
    private Date deletedAt; // Soft delete

    // Méthodes utilitaires
    public boolean isImage() {
        return this.contentType != null && this.contentType.startsWith("image/");
    }

    public boolean isPDF() {
        return "application/pdf".equals(this.contentType);
    }

    public void softDelete() {
        this.deletedAt = new Date();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public String getFormattedSize() {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
}