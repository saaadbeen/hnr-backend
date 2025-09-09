// src/main/java/com/example/HNR/DTO/Request/MissionCreateRequest.java
package com.example.HNR.DTO.Request;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class MissionCreateRequest {
    private String titre;
    private String description;
    private String prefecture;
    private String commune;
    private OffsetDateTime dateEnvoi;
    private String assignedUserId;
    private String changementId;
    private String geometryType;   // ex: "POLYGON"
    private String polygonWKT;     // ex: "POLYGON((lng lat, ...))"
}
