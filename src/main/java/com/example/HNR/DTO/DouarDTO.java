// src/main/java/com/example/HNR/DTO/DouarDTO.java
package com.example.HNR.DTO;

import com.example.HNR.Model.enums.StatutDouar;
import lombok.Data;
import java.util.Date;

@Data
public class DouarDTO {
    private Long id;
    private String nom;
    private StatutDouar statut;
    private String prefecture;
    private String commune;
    private Double latitude;
    private Double longitude;
    private Long missionId;       // si Douar -> Mission (ManyToOne) existe
    private String createdByUserId;
    private Date createdAt;
    private Date updatedAt;
}
