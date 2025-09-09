package com.example.HNR.Model.SqlServer;

import com.example.HNR.Model.enums.StatutDouar;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;
import org.locationtech.jts.geom.Polygon;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "douars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Douar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long douarId;

    @Column(nullable = false, length = 100)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDouar statut;

    @Column(nullable = false, length = 50)
    private String prefecture;

    @Column(nullable = false, length = 50)
    private String commune;

    // Relations
    @OneToMany(mappedBy = "douar", fetch = FetchType.LAZY)
    @JsonIgnore // ⬅️ on ne laisse JAMAIS Jackson sérialiser la collection
    private List<Action> actions = new ArrayList<>();



    @OneToMany(mappedBy = "douar", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Changement> changements = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Column(name = "created_by_user_id", length = 100, updatable = false)
    private String createdByUserId;





    // Métadonnées
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    // Géométrie (SRID 4326 côté DB)
    @Column(columnDefinition = "geometry")
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    private Polygon geometry;

    // Méthodes métier simples
    public void eradiquer() { this.statut = StatutDouar.ERADIQUE; }
    public void softDelete() { this.deletedAt = new Date(); }
}
