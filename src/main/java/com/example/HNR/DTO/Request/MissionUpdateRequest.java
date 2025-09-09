// src/main/java/com/example/HNR/DTO/Request/MissionUpdateRequest.java
package com.example.HNR.DTO.Request;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class MissionUpdateRequest {
    private String titre;
    private String description;
    private String prefecture;
    private String commune;
    private OffsetDateTime dateEnvoi;
    private String statut;         // PLANIFIEE / EN_COURS / TERMINEE / ANNULEE (string → enum au service)
    private String assignedUserId;
    private String changementId;
    private String geometryType;
    private String polygonWKT;
    private String rapportPdf;
}
