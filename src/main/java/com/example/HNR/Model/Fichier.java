package com.example.HNR.Model;


import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "fichiers")
@Data
public class Fichier {

    @Id
    private String id; // ID MongoDB

    private String NomFichier; // Nom fichier
    private String filePath; // Chemin fichier
    private String fileType; // Type fichier
    private long fileSize; // Taille fichier
    private Date Dateupload;
    private String uploadedBy; // ID utilisateur (référence User)
    private String entityType; // Type entité liée
    private String entityId; // ID entité liée
}
