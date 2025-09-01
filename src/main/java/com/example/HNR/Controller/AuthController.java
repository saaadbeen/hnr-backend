package com.example.HNR.Controller;

import com.example.HNR.Config.JwtTokenUtil;
import com.example.HNR.DTO.auth.LoginRequest;
import com.example.HNR.DTO.auth.RegisterRequest;
import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            response.put("status", "error");
            response.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String jwt = jwtTokenUtil.generateToken(user.getEmail(), user.getRole().name());

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
    }
    @PreAuthorize("hasRole('MEMBRE_DSI')")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Map<String, Object> response = new HashMap<>();
        if (userService.existsByEmail(registerRequest.getEmail())) {
            response.put("status", "error");
            response.put("message", "Cet email existe déjà");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        User newUser = new User();
        newUser.setFullName(registerRequest.getFullName());
        newUser.setEmail(registerRequest.getEmail());
        // L'encodage est géré dans UserServiceImpl#create pour éviter le double encodage
        newUser.setPassword(registerRequest.getPassword());
        newUser.setRole(registerRequest.getRole());
        newUser.setPrefecture(registerRequest.getPrefecture());
        newUser.setCommune(registerRequest.getCommune());
        newUser.setCreatedAt(new Date());

        User savedUser = userService.create(newUser);

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
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
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
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@Valid @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
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
    }
}

