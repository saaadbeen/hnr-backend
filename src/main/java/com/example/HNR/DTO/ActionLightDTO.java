package com.example.HNR.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionLightDTO {
    private Long actionId;
    private String type;

    /** Compat front : certains écrans lisent `action.date` d’autres `action.dateAction` */
    private Date date;        // = dateAction
    private Date dateAction;  // = date

    private String prefecture;
    private String commune;

    private Long douarId;     // pas d’objet Douar ici -> pas de lazy
    private Long missionId;

    private String photoAvantUrl;
    private String photoApresUrl;

    private String userId;    // email / id de l’agent
}
