package com.example.HNR.Controller;

import com.example.HNR.Model.Role;
import com.example.HNR.Model.User;
import com.example.HNR.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /api/users - Obtenir tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // GET /api/users/{id} - Obtenir utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/users/email/{email} - Obtenir utilisateur par email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/users/role/{role} - Obtenir utilisateurs par rôle
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        List<User> users = userService.findByRole(role);
        return ResponseEntity.ok(users);
    }

    // GET /api/users/prefecture/{prefecture} - Obtenir utilisateurs par préfecture
    @GetMapping("/prefecture/{prefecture}")
    public ResponseEntity<List<User>> getUsersByPrefecture(@PathVariable String prefecture) {
        List<User> users = userService.findByPrefectureCommune(prefecture);
        return ResponseEntity.ok(users);
    }

    // GET /api/users/role/{role}/prefecture/{prefecture} - Obtenir utilisateurs par rôle et préfecture
    @GetMapping("/role/{role}/prefecture/{prefecture}")
    public ResponseEntity<List<User>> getUsersByRoleAndPrefecture(@PathVariable Role role, @PathVariable String prefecture) {
        List<User> users = userService.findByRoleAndPrefectureCommune(role, prefecture);
        return ResponseEntity.ok(users);
    }

    // GET /api/users/search/{name} - Rechercher par nom
    @GetMapping("/search/{name}")
    public ResponseEntity<List<User>> searchUsersByName(@PathVariable String name) {
        List<User> users = userService.searchByName(name);
        return ResponseEntity.ok(users);
    }

    // POST /api/users - Créer nouvel utilisateur
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/users/{id} - Mettre à jour utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/users/{id}/password - Changer mot de passe
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String id, @RequestBody String newPassword) {
        try {
            userService.changePassword(id, newPassword);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/users/{id} - Supprimer utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/users/count/role/{role} - Compter par rôle
    @GetMapping("/count/role/{role}")
    public ResponseEntity<Long> countUsersByRole(@PathVariable Role role) {
        long count = userService.countByRole(role);
        return ResponseEntity.ok(count);
    }
}
