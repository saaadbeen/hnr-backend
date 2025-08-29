package com.example.HNR.Service;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Model.enums.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(User user);
    Optional<User> findById(String id);
    List<User> findAll();
    User update(User user);
    void delete(String id);

    // Méthodes métier spécifiques
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByLocation(String prefecture, String commune);
    boolean existsByEmail(String email);
}