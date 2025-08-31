package com.example.HNR.Controller;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Model.enums.Role;
import com.example.HNR.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
public class UserController {

    @Autowired
    private UserService userService;

    // GET all users - accessible aux GOUVERNEUR et MEMBRE_DSI
    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET user by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #id == authentication.name")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST create user - accessible aux GOUVERNEUR et MEMBRE_DSI
    @PostMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            // Vérifier si l'email existe déjà
            if (userService.existsByEmail(user.getEmail())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
            }

            // Définir la date de création
            user.setCreatedAt(new Date());

            User savedUser = userService.create(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update user
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #id == authentication.name")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            Optional<User> existingUser = userService.findById(id);
            if (existingUser.isPresent()) {
                // Vérifier si le nouvel email existe déjà (sauf pour l'utilisateur actuel)
                if (!existingUser.get().getEmail().equals(user.getEmail()) &&
                        userService.existsByEmail(user.getEmail())) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }

                user.setUserid(id);
                // Conserver la date de création originale
                user.setCreatedAt(existingUser.get().getCreatedAt());

                User updatedUser = userService.update(user);
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE user - accessible aux GOUVERNEUR uniquement
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                userService.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET user by email - utile pour l'authentification
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> user = userService.findByEmail(email);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET users by role
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        try {
            List<User> users = userService.findByRole(role);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET users by location
    @GetMapping("/location")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<List<User>> getUsersByLocation(
            @RequestParam String prefecture,
            @RequestParam String commune) {
        try {
            List<User> users = userService.findByLocation(prefecture, commune);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET check if email exists
    @GetMapping("/exists/{email}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}