package com.example.HNR.Model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter @Setter
@RequiredArgsConstructor
@Document(collection = "actions")
public class Action {
    @Id
    private String id;

    private Date dateAction;
    private TypeAction type;

    private String idUtilisateur;
    private String idDouar;
    private String idMission;

    private Date date;


    private String idPv;
}
