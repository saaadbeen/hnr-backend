package com.example.HNR.Service;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Model.enums.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User create(User user);
    Optional<User> findById(String id);
    Page<User> findAll(Pageable pageable);
    User update(User user);
    void delete(String id);

    // Méthodes métier spécifiques
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByLocation(String prefecture, String commune);
    boolean existsByEmail(String email);
}