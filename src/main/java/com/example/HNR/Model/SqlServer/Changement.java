package com.example.HNR.Model.SqlServer;

import com.example.HNR.Model.enums.TypeExtension;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

/**
 * Entité Changement stockée dans SQL Server
 * Historique des extensions et modifications
 */
@Entity
@Table(name = "changements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Changement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long changementId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeExtension type;

    @Column(name = "date_before", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateBefore;

    @Column(name = "date_after")
    @Temporal(TemporalType.DATE)
    private Date dateAfter;

    @Column(nullable = false)
    private Double surface;

    @Column(name = "photo_before", length = 500)
    private String photoBefore; // URL de la photo avant

    @Column(name = "photo_after", length = 500)
    private String photoAfter; // URL de la photo après

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "douar_id", nullable = false)
    private Douar douar;

    @OneToMany(mappedBy = "changement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Fichier> fichiers;

    // Référence vers l'utilisateur qui a détecté le changement (stocké dans MongoDB)
    @Column(name = "detected_by_user_id", length = 100)
    private String detectedByUserId;

    // Métadonnées
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    // Méthodes utilitaires
    public boolean isHorizontal() {
        return TypeExtension.HORIZONTAL.equals(this.type);
    }

    public boolean isVertical() {
        return TypeExtension.VERTICAL.equals(this.type);
    }

    public boolean hasPhotos() {
        return this.photoBefore != null && this.photoAfter != null;
    }
}