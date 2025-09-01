package com.example.HNR.Model.SqlServer;

import com.example.HNR.Model.enums.TypeAction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    @Column(name = "date_action", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private Date dateAction = new Date();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAction type;





    @Column(length = 50)
    private String prefecture;

    @Column(length = 50)
    private String commune;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "douar_id", nullable = false)
    private Douar douar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @OneToOne(mappedBy = "action", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PV pv;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    // Métadonnées
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "photo_avant_url", length = 512)
    private String photoAvantUrl;

    @Column(name = "photo_apres_url", length = 512)
    private String photoApresUrl;
    // Callbacks JPA
    @PrePersist
    @PreUpdate
    private void syncLocationFromDouar() {
        if (this.douar != null) {
            this.prefecture = this.douar.getPrefecture();
            this.commune = this.douar.getCommune();
        }
    }




}
