package com.example.HNR.Controller;

import com.example.HNR.Model.Utilisateur;

import com.example.HNR.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.HNR.Security.JwtUtil;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔐 Inscription (ajout utilisateur + hash du mot de passe)
    @PostMapping("/register")
    public String register(@RequestBody Utilisateur utilisateur) {
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            return "❌ Email déjà utilisé";
        }

        // 🔑 Hasher le mot de passe avant de le sauvegarder
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        utilisateurRepository.save(utilisateur);

        return "✅ Utilisateur enregistré avec succès";
    }

    // 🔑 Connexion (vérifie l’utilisateur et renvoie un token)
    @PostMapping("/login")
    public String login(@RequestBody Utilisateur loginRequest) {
        // Rechercher l'utilisateur par email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginRequest.getEmail())
                .orElse(null);

        // Vérifier les identifiants
        if (utilisateur != null &&
                passwordEncoder.matches(loginRequest.getPassword(), utilisateur.getPassword())) {

            // ✅ Générer un JWT
            String token = jwtUtil.generateToken(utilisateur.getEmail());
            return "Bearer " + token;
        }

        return "❌ Email ou mot de passe incorrect";
    }
}