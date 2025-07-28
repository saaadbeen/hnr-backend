package com.example.HNR.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.List;

 // Liste des IDs des douars

@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "missions")
public class Mission {
    @Id
    private String id;               // MongoDB ObjectId (string)

    private UUID idMission;          // UUID technique si besoin
    private Date dateCreation;
    private Date dateEnvoi;

    private String prefecture;
    private String commune;
    private List<String> idDouars;
    private String rapportPDF;       // URL vers le PDF du rapport

    private String creePar;          // id de l'utilisateur créateur
    private List<String> utilisateursAssignes; // ids des utilisateurs assignés

    private String statut;
    private int nombreDouars;
    private int nombreActions;
}