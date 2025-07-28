package com.example.HNR.Model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
@Document(collection = "utilisateurs")
public class Utilisateur {
    @Id
    private String id;

    private UUID idUtilisateur;      // identifiant métier
    private String nom;
    private String prenom;

    private String prefecture;
    private String commune;

    private Role role;

    private String email;
    private String password;

    private Date dateCreation;
}
