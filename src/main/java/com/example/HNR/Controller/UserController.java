// UserController.java
package com.example.HNR.Controller;

import com.example.HNR.DTO.UserDTO;
import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users
    @GetMapping
    public List<UserDTO> list() {
        return userService.findAll().stream().map(UserController::toDTO).toList();
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> get(@PathVariable String id) {
        Optional<User> u = userService.findById(id);
        return u.map(value -> ResponseEntity.ok(toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/users/location?prefecture=...&commune=...
    @GetMapping("/location")
    public List<UserDTO> byLocation(@RequestParam String prefecture,
                                    @RequestParam(required = false) String commune) {
        List<User> users = (commune == null || commune.isBlank())
                ? userService.findByPrefecture(prefecture)
                : userService.findByPrefectureAndCommune(prefecture, commune);
        return users.stream().map(UserController::toDTO).toList();
    }

    // GET /api/users/exists/{email}
    @GetMapping("/exists/{email:.+}") // accepte les points dans l'email
    public ResponseEntity<Boolean> exists(@PathVariable String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean deleted = userService.deleteByUserid(id);   // ➜ ajoute cette méthode côté service
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
    // ---------- Mapping utilitaire ----------
    private static UserDTO toDTO(User u) {
        UserDTO d = new UserDTO();
        d.setUserid(u.getUserid());            // on conserve bien 'userid'
        d.setFullName(u.getFullName());
        d.setEmail(u.getEmail());
        d.setRole(u.getRole());               // type enum, conforme à ton UserDTO
        d.setPrefecture(u.getPrefecture());
        d.setCommune(u.getCommune());
        d.setCreatedAt(u.getCreatedAt());
        return d;
    }
}
