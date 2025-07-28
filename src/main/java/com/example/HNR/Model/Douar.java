package com.example.HNR.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Getter @Setter
@RequiredArgsConstructor
@Document(collection = "douars")
public class Douar {
    @Id
    private String idDouar;

    private String nom;
    private StatutDouar statut;

    private String prefecture;
    private String commune;

    private List<Date> visites;      // dates de visite

    private String idMission;        // mission à laquelle le douar appartient

    private int nombreChangements;
    private int nombreActions;
}