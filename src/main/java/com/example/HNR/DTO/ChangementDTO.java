package com.example.HNR.DTO;

import com.example.HNR.Model.enums.TypeExtension;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangementDTO {
    public Long          changementId;
    public TypeExtension type;
    public Date          date;

    public Long          douarId;
    public String        detectedByUserId;

    public String        titre;
    public String        description;
    public String        prefecture;
    public String        commune;
    public String        pdfUrl;

    // 🗺️ Position
    public Double        longitude;
    public Double        latitude;

    // Accepte "pointWKT" venant du front
    @JsonAlias({"pointWKT","point_wkt"})
    public String        pointWKT;
    public String polygonWKT;

    // Métadonnées (lecture seule)
    public Date          createdAt;
    public Date          updatedAt;
}
