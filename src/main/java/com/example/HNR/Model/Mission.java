package com.example.HNR.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor //  constructeur avec tous les arguments
@NoArgsConstructor  // constructeur sans argument
@Document(collection = "missions")
public class Mission {

    @Id
    private String id;
    private String nom;
    private String rapportUrl;         // URL vers le fichier PDF (au lieu de byte[])
    private Date dateEnvoi;
    private String prefecture;
    private String commune;
    private String createurId;
    private List<Douar> douars;
}
