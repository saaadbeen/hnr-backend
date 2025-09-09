// src/main/java/com/example/HNR/DTO/MissionDTO.java
package com.example.HNR.DTO;

import com.example.HNR.Model.enums.Statut;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class MissionDTO {
    private Long missionId;     // ← exposé au front
    private String titre;
    private String description;
    private String prefecture;
    private String commune;
    private OffsetDateTime dateEnvoi;
    private Statut statut;

    private String assignedUserId;
    private String createdByUserId;
    private String changementId;

    private String geometryType;
    private String polygonWKT;

    private String rapportPdf;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
