// src/main/java/com/example/HNR/Model/SqlServer/Mission.java
package com.example.HNR.Model.SqlServer;

import com.example.HNR.Model.enums.Statut;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Entity
@Table(name = "missions")
@Getter @Setter @ToString
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long missionId; // ← nouveau nom du champ identifiant

    @Column(name="titre", nullable = false, length = 255)
    private String titre;

    @Column(name="description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name="prefecture", nullable = false, length = 150)
    private String prefecture; // ex: "Préfecture de Ben M'sick"

    @Column(name="commune", nullable = false, length = 120)
    private String commune;

    @Column(name="date_envoi", nullable = false)
    private OffsetDateTime dateEnvoi;

    @Enumerated(EnumType.STRING)
    @Column(name="statut", nullable = false, length = 30)
    private Statut statut;

    @Column(name="assigned_user_id", nullable = false, length = 64)
    private String assignedUserId;

    @Column(name="created_by_user_id", nullable = false, length = 64)
    private String createdByUserId;

    @Column(name="changement_id", nullable = false, length = 64)
    private String changementId;

    @Column(name="geometry_type", nullable = false, length = 30)
    private String geometryType; // "POLYGON"

    @Column(name="polygonwkt", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String polygonWKT;

    @Column(name="rapportpdf", columnDefinition = "NVARCHAR(MAX)")
    private String rapportPdf;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
