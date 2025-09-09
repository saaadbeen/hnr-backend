package com.example.HNR.Model.SqlServer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "pvs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pv_id")
    private Long pvId;

    @OneToOne(optional = false)
    @JoinColumn(name = "action_id", nullable = false, unique = true)
    private Action action;

    // JSON (titre, constatations, decisions, photos…) sérialisé en String
    @Lob
    // NVARCHAR(MAX) est plus sûr côté SQL Server que TEXT (déprécié)
    @Column(name = "contenu", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String contenu = "{}";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_redaction", nullable = false)
    private Date dateRedaction = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "valide", nullable = false)
    private Boolean valide = false;

    @Column(name = "validateur_user_id")
    private String validateurUserId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_validation")
    private Date dateValidation;

    @Column(name = "redacteur_user_id")
    private String redacteurUserId;
}
