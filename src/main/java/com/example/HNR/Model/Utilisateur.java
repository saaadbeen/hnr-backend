package com.example.HNR.Model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Data                       // génère getter, setter, toString, equals, hashCode
@AllArgsConstructor //  constructeur avec tous les arguments
@NoArgsConstructor  // constructeur sans argument
@Builder                    // créer des objets de manière fluide et immuable
@Document(collection = "utilisateurs")
public class Utilisateur {
    @Id
    private String id;

    private String nom;
    private String prenom;

    private String prefecture;
    private String commune;

    private Role role;

    private String email;
    private String password;

}
