package com.example.HNR.DTO.Request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DouarCreateRequest {
    @NotBlank private String nom;
    @NotBlank private String prefecture;
    @NotBlank private String commune;

    private String statut;

    @NotNull  private JsonNode geometry; // GeoJSON Polygon
}
