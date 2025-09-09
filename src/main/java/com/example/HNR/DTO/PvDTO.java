package com.example.HNR.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.Map;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PvDTO {
    /** Alias pour compatibilité : certains écrans utilisent id, d’autres pvId */
    private Long id;
    private Long pvId;

    private Long actionId;
    private String type;      // type de l’action (DEMOLITION, SIGNALEMENT, ...)
    private String statut;    // BROUILLON | PUBLIE
    private String numero;    // si tu en as un côté entité, sinon null

    private Map<String, Object> contenu; // JSON (titre, constatations, decisions, photos, ...)
    private Date createdAt;
    private Date updatedAt;

    private ActionLightDTO action; // version light, jamais d’entités Hibernate
}
