package com.example.HNR.Model.SqlServer;
import com.example.HNR.Model.enums.Statut;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "missions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long missionId;

    @Column(name = "titre", length = 200, nullable = false)
    private String titre;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 30, nullable = false)
    private Statut statut = Statut.EN_COURS; // Valeur par défaut

    @Column(name = "prefecture", length = 100)
    private String prefecture;

    @Column(name = "commune", length = 100)
    private String commune;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_envoi", updatable = false)
    private Date dateEnvoi;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completed_at")
    private Date completedAt;

    @Column(name = "rapport_pdf", length = 512)
    private String rapportPDF;

    // Référence (string) vers l'id utilisateur MongoDB inscrit dans le JWT
    @Column(name = "created_by_user_id", length = 64, updatable = false)
    private String createdByUserId;

    @ElementCollection
    @CollectionTable(name = "mission_assigned_users", joinColumns = @JoinColumn(name = "mission_id"))
    @Column(name = "user_id")
    private List<String> assignedUserIds = new ArrayList<>();

    // Relations (ex: actions)
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> actions = new ArrayList<>();

    public Mission() {}

    // === Mapping JSON côté Front ===
    @JsonProperty("id")
    public Long getIdJson() { return missionId; }

    @JsonProperty("status")
    public String getStatusJson() { return statut != null ? statut.name() : null; }

    // === Getters/Setters requis par les contrôleurs/services ===
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Bridge String <-> Enum pour compatibilité existante
    public String getStatut() { return statut != null ? statut.name() : null; }
    public void setStatut(String statut) {
        if (statut == null) {
            this.statut = null;
        } else {
            try {
                this.statut = Statut.valueOf(statut);
            } catch (IllegalArgumentException ex) {
                this.statut = this.statut != null ? this.statut : Statut.EN_COURS;
            }
        }
    }

    public Statut getStatutEnum() { return statut; }
    public void setStatutEnum(Statut statut) { this.statut = statut; }

    public String getPrefecture() { return prefecture; }
    public void setPrefecture(String prefecture) { this.prefecture = prefecture; }

    public String getCommune() { return commune; }
    public void setCommune(String commune) { this.commune = commune; }

    public Date getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(Date dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public String getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(String createdByUserId) { this.createdByUserId = createdByUserId; }

    public List<String> getAssignedUserIds() { return assignedUserIds; }
    public void setAssignedUserIds(List<String> assignedUserIds) { this.assignedUserIds = assignedUserIds; }

    public String getRapportPDF() { return rapportPDF; }
    public void setRapportPDF(String rapportPDF) { this.rapportPDF = rapportPDF; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Date getCompletedAt() { return completedAt; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }

    public boolean isCompleted() { return this.statut == Statut.TERMINEE || this.completedAt != null; }

    public void complete() {
        this.statut = Statut.TERMINEE;
        if (this.completedAt == null) {
            this.completedAt = new Date();
        }
    }
}

