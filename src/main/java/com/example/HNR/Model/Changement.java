package com.example.HNR.Model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter @Setter
@RequiredArgsConstructor
@Document(collection = "changements")
public class Changement {
    @Id
    private String id;

    private String codeChangement;
    private TypeChangement type;

    private Date dateAvant;
    private Date dateApres;

    private String photoAvant;
    private String photoApres;

    private double surface;
    private String idDouar;
}

