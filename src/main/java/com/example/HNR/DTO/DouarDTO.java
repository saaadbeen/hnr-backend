package com.example.HNR.DTO;

import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.StatutDouar;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DouarDTO {
    private Long id;
    private String nom;
    private StatutDouar statut;              // si null -> NON_ERADIQUE par défaut côté service
    private String prefecture;
    private String commune;

    private Long missionId;                  // exposé à plat (relation en entité)
    private String createdByUserId;



    // Géométrie (E/S)
    private String geometryWKT;              // "POLYGON((lng lat,...))"
    private List<List<Double>> coordinates;  // anneau extérieur: [[lng,lat], ...]
    private String geometryType;             // "Polygon"
    private Double centerLng; // centroïde X (lon)
    private Double centerLat;
    private Date createdAt;
    private Date updatedAt;


}
