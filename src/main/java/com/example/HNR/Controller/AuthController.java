package com.example.HNR.Controller;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Model.enums.Role;
import com.example.HNR.Service.UserService;
import com.example.HNR.Config.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // POST login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Vérifier si l'utilisateur existe
            Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());
            if (!userOptional.isPresent()) {
                response.put("status", "error");
                response.put("message", "Email ou mot de passe incorrect");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = userOptional.get();

            // Vérifier le mot de passe
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                response.put("status", "error");
                response.put("message", "Email ou mot de passe incorrect");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Authentification réussie - générer le token JWT
            String jwt = jwtTokenUtil.generateToken(user.getEmail(), user.getRole().name());

            // Préparer les informations utilisateur
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userid", user.getUserid());
            userInfo.put("fullName", user.getFullName());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole().name());
            userInfo.put("prefecture", user.getPrefecture());
            userInfo.put("commune", user.getCommune());

            response.put("status", "success");
            response.put("message", "Connexion réussie");
            response.put("token", jwt);
            response.put("user", userInfo);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur interne du serveur");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // POST register
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Vérifier si l'email existe déjà
            if (userService.existsByEmail(registerRequest.getEmail())) {
                response.put("status", "error");
                response.put("message", "Cet email existe déjà");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Créer le nouvel utilisateur
            User newUser = new User();
            newUser.setFullName(registerRequest.getFullName());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPassword(registerRequest.getPassword()); // Le service se charge de l'encoder
            newUser.setRole(registerRequest.getRole());
            newUser.setPrefecture(registerRequest.getPrefecture());
            newUser.setCommune(registerRequest.getCommune());
            newUser.setCreatedAt(new Date());

            User savedUser = userService.create(newUser);

            // Préparer les informations utilisateur
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userid", savedUser.getUserid());
            userInfo.put("fullName", savedUser.getFullName());
            userInfo.put("email", savedUser.getEmail());
            userInfo.put("role", savedUser.getRole().name());
            userInfo.put("prefecture", savedUser.getPrefecture());
            userInfo.put("commune", savedUser.getCommune());

            response.put("status", "success");
            response.put("message", "Utilisateur créé avec succès");
            response.put("user", userInfo);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la création de l'utilisateur");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // GET current user info
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("status", "error");
                response.put("message", "Token manquant");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = authHeader.substring(7);
            if (!jwtTokenUtil.validateToken(token)) {
                response.put("status", "error");
                response.put("message", "Token invalide");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            Optional<User> userOptional = userService.findByEmail(userEmail);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("userid", user.getUserid());
                userInfo.put("fullName", user.getFullName());
                userInfo.put("email", user.getEmail());
                userInfo.put("role", user.getRole().name());
                userInfo.put("prefecture", user.getPrefecture());
                userInfo.put("commune", user.getCommune());

                return ResponseEntity.ok(userInfo);
            } else {
                response.put("status", "error");
                response.put("message", "Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des informations");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // POST refresh token
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = request.get("token");

            if (jwtTokenUtil.validateToken(token)) {
                String userEmail = jwtTokenUtil.getUsernameFromToken(token);
                String role = jwtTokenUtil.getRoleFromToken(token);
                String newToken = jwtTokenUtil.generateToken(userEmail, role);

                response.put("status", "success");
                response.put("message", "Token renouvelé avec succès");
                response.put("token", newToken);

                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Token invalide");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors du renouvellement du token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Classes pour les requêtes
    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String fullName;
        private String email;
        private String password;
        private Role role;
        private String prefecture;
        private String commune;

        public RegisterRequest() {}

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }

        public String getPrefecture() { return prefecture; }
        public void setPrefecture(String prefecture) { this.prefecture = prefecture; }

        public String getCommune() { return commune; }
        public void setCommune(String commune) { this.commune = commune; }
    }
}