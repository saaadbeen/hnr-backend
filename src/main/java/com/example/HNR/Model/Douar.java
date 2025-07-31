package com.example.HNR.Model;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;


@AllArgsConstructor //  constructeur avec tous les arguments
@NoArgsConstructor  // constructeur sans argument
@Document(collection = "douars")
@Data// génère getter, setter, toString, equals, hashCode
public class Douar {
    @Id
    private String id;
    private String nom;
    private StatutDouar statut;    // ✅ enum
    private List<Date> dateVisites;
    private List<String> missionIds;
    private int nombreChangements;
    private int nombreActions;
}