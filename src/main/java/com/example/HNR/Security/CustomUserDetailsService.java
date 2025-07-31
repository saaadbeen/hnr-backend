package com.example.HNR.Security;

import com.example.HNR.Model.Utilisateur;
import com.example.HNR.Repository.UtilisateurRepository;

import com.example.HNR.Model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // 🔍 Méthode appelée automatiquement pour charger l'utilisateur depuis la base
    @Override

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return new org.springframework.security.core.userdetails.User(
                utilisateur.getEmail(),
                utilisateur.getPassword(),
                new ArrayList<>() // liste vide d'authorities (ou adapter avec les rôles si tu veux)
        );
    }

}
