package com.example.HNR.Model.SqlServer;

import com.example.HNR.Model.enums.TypeExtension;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "changements")
public class Changement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "changement_id")
    private Long changementId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TypeExtension type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "douar_id")
    private Douar douar;

    @Column(name = "detected_by_user_id", length = 100)
    private String detectedByUserId;

    @Column(name = "titre", length = 150)
    private String titre;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "prefecture", length = 150)
    private String prefecture;

    @Column(name = "commune", length = 150)
    private String commune;

    @Column(name = "pdf_url")
    private String pdfUrl;

    // 🗺️ Position
    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "point_wkt")
    private String pointWkt;

    @Column(name = "polygon_wkt")
    private String polygonWkt;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
    @PrePersist
    public void prePersist() {
        Date now = new Date();
        if (createdAt == null) createdAt = now;
        if (date == null) date = now;

        // Si on a lng/lat mais pas de WKT point → le générer
        if ((pointWkt == null || pointWkt.isBlank()) && longitude != null && latitude != null) {
            pointWkt = "POINT(" + longitude + " " + latitude + ")";
        }

        // Si on a un polygone mais pas de centroïde → tenter de déduire lng/lat (centroïde)
        if ((longitude == null || latitude == null) && polygonWkt != null && !polygonWkt.isBlank()) {
            try {
                Geometry g = new WKTReader().read(polygonWkt);
                Coordinate c = g.getCentroid().getCoordinate();
                if (c != null) { longitude = c.x; latitude = c.y; }
            } catch (Exception ignored) {}
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
        // garder la cohérence du point si lng/lat ont changé
        if (longitude != null && latitude != null) {
            pointWkt = "POINT(" + longitude + " " + latitude + ")";
        }
    }
}
