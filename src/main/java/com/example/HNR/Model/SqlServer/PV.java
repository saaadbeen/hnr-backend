package com.example.HNR.Model.SqlServer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * Entité PV (Procès-Verbal) stockée dans SQL Server
 * Historique des procès-verbaux
 */
@Entity
@Table(name = "pvs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pvId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_redaction", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRedaction;



    @Column(nullable = false)
    private boolean valide = false;

    @Column(name = "url_pdf", length = 500)
    private String urlPDF;

    // Relation avec Action
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id")
    private Action action;

    // Référence vers l'utilisateur rédacteur (stocké dans MongoDB)
    @Column(name = "redacteur_user_id", nullable = false, length = 100)
    private String redacteurUserId;

    // Référence vers l'utilisateur validateur (stocké dans MongoDB)
    @Column(name = "validateur_user_id", length = 100)
    private String validateurUserId;

    @Column(name = "date_validation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateValidation;

    // Métadonnées
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    // Méthodes utilitaires
    public void valider(String validateurUserId) {
        this.valide = true;
        this.validateurUserId = validateurUserId;
        this.dateValidation = new Date();
    }

}