package com.example.HNR.Model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;



@Document(collection = "pvs") // Collection MongoDB "pvs"
@Data
public class PV {

    @Id
    private String idPV; // ID MongoDB

    private String contenu; // Contenu PV
    private Date dateRedaction; // Date rédaction
    private String redacteur; // ID rédacteur (référence User)
    private String numero; // Numéro unique PV
    private boolean valide; // PV validé ?
    private String urlPDF; // URL fichier PDF
}