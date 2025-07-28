package com.example.HNR.Controller;

import com.example.HNR.Model.Utilisateur;
import com.example.HNR.Model.Role;
import com.example.HNR.repository.UtilisateurRepository;
import com.example.HNR.Security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private UtilisateurRepository userRepo;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    // DTO login
    static class LoginRequest { public String email, password; }
    // DTO register : on ajoute nom/prenom, on remplace login→email
    static class RegisterRequest {
        public String nom;
        public String prenom;
        public String email;
        public String password;
        public String role;
        public String prefecture;
        public String commune;
    }
    // DTO réponse token
    static class JwtResponse { public String token; public JwtResponse(String t){ token = t; } }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.findByEmail(req.email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email déjà utilisé");
        }
        Utilisateur u = new Utilisateur();
        u.setIdUtilisateur(UUID.randomUUID());
        u.setDateCreation(new Date());
        u.setNom(req.nom);
        u.setPrenom(req.prenom);
        u.setEmail(req.email);
        u.setPassword(passwordEncoder.encode(req.password));
        u.setRole(Role.valueOf(req.role.toUpperCase()));
        u.setPrefecture(req.prefecture);
        u.setCommune(req.commune);
        userRepo.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email, req.password)
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiants invalides");
        }
        // Génère le token à partir de l'email et du rôle
        Utilisateur u = userRepo.findByEmail(req.email).get();
        String token = jwtUtils.generateToken(u.getEmail(), u.getRole().name());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
