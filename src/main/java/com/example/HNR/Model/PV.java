package com.example.HNR.Model;
import lombok.RequiredArgsConstructor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter @Setter
@RequiredArgsConstructor
@Document(collection = "pvs")
public class PV {
    @Id
    private String id;

    private String idDouar;
    private String idUtilisateur;

    private Date dateRedaction;
    private String contenu;

    private String numero;
    private boolean valide;
    private String signature;        // URL ou chaîne de signature
    private String urlPDF;           // URL du PDF généré
}
