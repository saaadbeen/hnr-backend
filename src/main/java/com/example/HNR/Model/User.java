package com.example.HNR.Model;
import com.example.HNR.Model.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Document(collection = "users") // Collection MongoDB "users"
@Data // Génère getters, setters automatiquement
public class User implements UserDetails { // Pour Spring Security

    @Id
    private String id; // ID MongoDB

    private String fullName; // Nom complet
    private String email; // Email unique

    @JsonIgnore // Ne pas exposer dans API
    private String password; // Mot de passe hashé

    private Role role; // Rôle utilisateur
    private String prefectureCommune; // Zone géographique
    private Date createdAt; // Date création

    // Méthodes Spring Security (obligatoires)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    @JsonIgnore
    public String getPassword() { return password; }
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
    @Override
    public String getUsername() { return email; }


}