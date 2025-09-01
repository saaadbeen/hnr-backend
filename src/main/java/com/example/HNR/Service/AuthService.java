package com.example.HNR.Service;

import com.example.HNR.Config.JwtTokenUtil;
import com.example.HNR.DTO.UserDTO;
import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Model.enums.Role;
import com.example.HNR.Repository.Mongodb.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Authentifier un utilisateur et générer un token JWT
     */
    public String authenticate(String email, String password) {
        try {
            // Vérifier si l'utilisateur existe
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                throw new BadCredentialsException("Utilisateur non trouvé");
            }

            User user = userOptional.get();

            // Vérifier le mot de passe
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Mot de passe incorrect");
            }

            // Créer l'authentification Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Générer le token JWT avec le rôle
            return jwtTokenUtil.generateToken(email, user.getRole().name());

        } catch (Exception e) {
            throw new BadCredentialsException("Erreur d'authentification: " + e.getMessage());
        }
    }

    /**
     * Créer un nouvel utilisateur
     */
    public UserDTO register(String fullName, String email, String password,
                            Role role, String prefecture, String commune) {

        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        // Créer le nouvel utilisateur
        User user = new User();
        user.setUserid(generateUserId());
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setPrefecture(prefecture);
        user.setCommune(commune);
        user.setCreatedAt(new Date());

        // Sauvegarder
        User savedUser = userRepository.save(user);

        // Convertir en DTO
        return convertToDTO(savedUser);
    }

    /**
     * Obtenir les informations utilisateur par email
     */
    public UserDTO getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
        return convertToDTO(userOptional.get());
    }

    /**
     * Extraire le nom d'utilisateur du token
     */
    public String getUsernameFromToken(String token) {
        try {
            return jwtTokenUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("Token invalide");
        }
    }

    /**
     * Valider un token JWT
     */
    public boolean validateToken(String token, String username) {
        try {
            return jwtTokenUtil.validateToken(token, username);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Encoder un mot de passe (pour les tests)
     */
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Vérifier un mot de passe (pour les tests)
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Obtenir l'utilisateur actuellement connecté
     */
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }

        String email = authentication.getName();
        return getUserByEmail(email);
    }

    /**
     * Vérifier si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String email, Role role) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.isPresent() && userOptional.get().getRole() == role;
    }

    /**
     * Changer le mot de passe d'un utilisateur
     */
    public void changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        User user = userOptional.get();

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        // Mettre à jour avec le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Méthodes utilitaires privées

    private String generateUserId() {
        return "USER_" + System.currentTimeMillis();
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserid(user.getUserid());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setPrefecture(user.getPrefecture());
        dto.setCommune(user.getCommune());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
