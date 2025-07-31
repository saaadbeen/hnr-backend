package com.example.HNR.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Document(collection = "fichiers")
@Data
@AllArgsConstructor //  constructeur avec tous les arguments
@NoArgsConstructor  // constructeur sans argument
@Builder
public class Fichier {

    @Id
    private String id;
    private String nom;
    private String chemin;
    private String missionId;
    private String changementId;

}