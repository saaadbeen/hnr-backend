package com.example.HNR.Model;
import com.example.HNR.Model.TypeExtension;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.Date;

@Document("changements")
@Data // Génère automatiquement getters, setters, toString, equals, hashCode
public class Changement {
    @Id
    private String codeChangement;
    private TypeExtension type;              // EXTENSION_HORIZONTALE | EXTENSION_VERTICAL
    private Date dateAvant;
    private String photoAvant;                // URL
    private String photoApres;                // URL
    private String douarId;
    private double surface;
    private Date dateApres;
}