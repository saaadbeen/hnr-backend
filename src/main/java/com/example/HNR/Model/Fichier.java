package com.example.HNR.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Getter @Setter
@RequiredArgsConstructor
@Document(collection = "fichiers")
public class Fichier {

    @Id
    private String id;
    private String nom;       // nom du fichier (ex : "photo.jpg")
    private String chemin;    // chemin ou URL de stockage
    private String idDouar;   // référence au douar


}
