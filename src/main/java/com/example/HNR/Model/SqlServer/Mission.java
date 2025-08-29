package com.example.HNR.Model.SqlServer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

/**
 * Entité Mission stockée dans SQL Server
 * Gestion des missions de terrain avec historique
 */
@Entity
@Table(name = "missions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long missionId;

    @Column(name = "date_envoi", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEnvoi;

    @Column(nullable = false, length = 50)
    private String prefecture;

    @Column(nullable = false, length = 50)
    private String commune;

    @Column(name = "rapport_pdf", length = 500)
    private String rapportPDF; // URL du rapport

    @Column(nullable = false, length = 50)
    private String statut;

    // Relations
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Douar> douars;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Action> actions;

    // Références vers les utilisateurs (stockés dans MongoDB)
    @Column(name = "created_by_user_id", nullable = false, length = 100)
    private String createdByUserId;

    @ElementCollection
    @CollectionTable(name = "mission_assigned_users",
            joinColumns = @JoinColumn(name = "mission_id"))
    @Column(name = "user_id", length = 100)
    private List<String> assignedUserIds;

    // Métadonnées
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;



    @Column(name = "completed_at")
    private Date completedAt;

    // Méthodes utilitaires
    public boolean isCompleted() {
        return "TERMINEE".equalsIgnoreCase(this.statut);
    }

    public void complete() {
        this.statut = "TERMINEE";
        this.completedAt = new Date();
    }

}