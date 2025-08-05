package com.example.HNR.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
    import com.example.HNR.Model.StatutDouar;

import java.util.List;

@Document(collection = "douars") // Collection MongoDB "douars"
@Data
public class Douar {

    @Id
    private String idDouar; // ID MongoDB

    private String nom; // Nom du douar
    private StatutDouar statut; // Statut (ERADIQUE/NON_ERADIQUE)
    private String prefectureCommune; // Zone administrative
    private List<String> visites; // IDs des visites (références Mission)
    private String idMission; // ID mission associée (référence Mission)
    private List<String> changements; // IDs changements (références Change)
    private List<String> actions; // IDs actions (références Action)
}
