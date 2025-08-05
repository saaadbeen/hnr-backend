package com.example.HNR.Controller;

import com.example.HNR.Config.JwtUtil;
import com.example.HNR.Model.User;
import com.example.HNR.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    // POST /api/auth/login - Connexion utilisateur
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> user = authService.authenticate(email, password);
        if (user.isPresent()) {
            String token = jwtUtil.generateToken(user.get());
            return ResponseEntity.ok(Map.of("token", token, "user", user.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou mot de passe incorrect");
        }
    }

    // POST /api/auth/register - Inscription nouvel utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = authService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/auth/change-password - Changer de mot de passe
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        try {
            String userId = passwordData.get("userId");
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            authService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok("Mot de passe changé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/auth/reset-password - Réinitialisation de mot de passe
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> resetData) {
        try {
            String userId = resetData.get("userId");
            String newPassword = resetData.get("newPassword");
            authService.resetPassword(userId, newPassword);
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/auth/check-email/{email} - Vérifier si email existe
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(exists);
    }


}