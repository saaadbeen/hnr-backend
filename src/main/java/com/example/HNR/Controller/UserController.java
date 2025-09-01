package com.example.HNR.Controller;
import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
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
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@userServiceImpl.findById(#id).isPresent() and @userServiceImpl.findById(#id).get().getEmail() == authentication.name)")
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
