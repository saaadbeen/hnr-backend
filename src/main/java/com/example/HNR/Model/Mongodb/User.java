// User.java - Modèle MongoDB corrigé
package com.example.HNR.Model.Mongodb;

import com.example.HNR.Model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String userid;

    private String fullName;

    @Indexed(unique = true)
    private String email;

    private String password;

    private Role role;

    private String prefecture;

    private String commune;

    private Date createdAt;

    // Constructeur avec paramètres (garde pour compatibilité)
    public User(String userid, String fullName, String email, Role role, String prefecture, String commune) {
        this.userid = userid;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.prefecture = prefecture;
        this.commune = commune;
        this.createdAt = new Date();
    }
}