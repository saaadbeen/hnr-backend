package com.example.HNR.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.*;



import java.util.Date;

@Document(collection = "actions")
@Data
@AllArgsConstructor //  constructeur avec tous les arguments
@NoArgsConstructor  // constructeur sans argument
@Builder
public class Action {
    @Id
    private String id;

    private Date dateAction;
    private TypeAction type;

    private Utilisateur utilisateur;
    private String idDouar;
    private String idMission;

    private Date date;


    private String idPv;
}