package com.example.HNR.Security;

import com.example.HNR.Model.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// Cette classe adapte Utilisateur à l'interface UserDetails de Spring Security
@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    // ➕ Accès à l'objet utilisateur original (utile dans certains cas)
    private final Utilisateur utilisateur;

    //  Rôle de l'utilisateur sous forme d'autorité (ex: ROLE_AGENT_AUTORITE)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())
        );
    }

    //  Mot de passe (servira à la vérification au login)
    @Override
    public String getPassword() {
        return utilisateur.getPassword();
    }

    //  L’identifiant utilisé pour se connecter (email dans ton cas)
    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }

    //  Ces méthodes peuvent toutes retourner true par défaut
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
