package com.example.HNR.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.*;

import lombok.RequiredArgsConstructor;

import lombok.Getter;
import lombok.Setter;


import java.util.Date;

@Data                       // génère getter, setter, toString, equals, hashCode
@AllArgsConstructor //  constructeur avec tous les arguments
@NoArgsConstructor  // constructeur sans argument@Builder                    // pour .builder() fluent API
@Document(collection = "pvs")
public class PV {
    @Id
    private String id;

    private Date dateRedaction;
    private String contenu;

    private String signature;        // URL ou chaîne de signature
    private String urlPDF;// URL du PDF généré

    private String idDouar;
    private String idUtilisateur;
}