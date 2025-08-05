package com.example.HNR.Service;

import com.example.HNR.Model.User;
import com.example.HNR.Model.Role;
import com.example.HNR.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Implémentation de UserDetailsService pour Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec email: " + email));
    }

    // ===== MÉTHODES PRINCIPALES =====

    // Créer un utilisateur
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(new Date());
        return userRepository.save(user);
    }

    // ===== MÉTHODES POUR COMPATIBILITÉ CONTROLLER =====

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getUsersByPrefecture(String prefecture) {
        return userRepository.findByPrefectureCommune(prefecture);
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name);
    }

    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public void changePassword(String userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ===== MÉTHODES POUR COMPATIBILITÉ SERVICE =====

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> findByPrefectureCommune(String prefectureCommune) {
        return userRepository.findByPrefectureCommune(prefectureCommune);
    }

    public List<User> findByRoleAndPrefectureCommune(Role role, String prefectureCommune) {
        return userRepository.findByRoleAndPrefectureCommune(role, prefectureCommune);
    }

    public List<User> searchByName(String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name);
    }

    public User updateUser(String id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + id));

        if (userDetails.getFullName() != null) {
            user.setFullName(userDetails.getFullName());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Un utilisateur avec cet email existe déjà");
            }
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
        if (userDetails.getPrefectureCommune() != null) {
            user.setPrefectureCommune(userDetails.getPrefectureCommune());
        }

        return userRepository.save(user);
    }

    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}