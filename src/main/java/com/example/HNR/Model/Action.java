package com.example.HNR.Model;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.Date;


@Document(collection = "actions") // Collection MongoDB "actions"
@Data
public class Action {

    @Id
    private String idAction; // ID MongoDB

    private Date dateAction; // Date d'action
    private TypeAction type; // Type action (DEMOLITION)
    private String utilisateurDouarId; // ID utilisateur (référence User)
    private String douarId; // ID douar (référence Douar)
    private String missionId; // ID mission (référence Mission)
    private String pvId; // ID procès-verbal (référence PV)
    private Date dateDebut; // Date début
    private String avisPrefecture; // Avis préfecture
    private String prefectureCommune; // ID préfecture (référence User)
}