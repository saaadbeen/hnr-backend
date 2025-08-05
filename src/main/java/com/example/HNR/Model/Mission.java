package com.example.HNR.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Document(collection = "missions")
@Data // Génère automatiquement getters, setters, toString, equals, hashCode
public class Mission {

    @Id
    private String idMission; // ID MongoDB

    private Date dateCreation; // Date création
    private Date dateEnvoi; // Date envoi
    private String prefectureCommune; // Zone géographique
    private String rapportPDF; // URL rapport PDF
    private String creePar; // ID créateur (référence User)
    private List<String> utilisateursAssignes; // IDs utilisateurs assignés (références User)
    private String statut; // Statut mission
    private int nombreDouars; // Nombre douars
    private int nombreActions; // Nombre actions
}