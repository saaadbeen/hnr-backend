package com.example.HNR.Service;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Model.enums.Role;
import com.example.HNR.Repository.Mongodb.UserRepository;
import com.example.HNR.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Méthodes existantes

    @Override
    public User create(User user) {
        try {
            // Encoder le mot de passe s'il n'est pas déjà encodé
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // Générer un ID unique si non fourni
            if (user.getUserid() == null || user.getUserid().isEmpty()) {
                user.setUserid(generateUserId());
            }

            // Définir la date de création si non fournie
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(new Date());
            }

            User savedUser = userRepository.save(user);
            log.info("User created successfully: {} ({})", savedUser.getEmail(), savedUser.getRole());
            return savedUser;

        } catch (Exception e) {
            log.error("Error creating user {}: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création de l'utilisateur", e);
        }
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    @Override
    public boolean deleteByUserid(String userid) {
        return userRepository.findByUserid(userid)       // assure-toi d’avoir cette méthode
                .map(u -> { userRepository.delete(u); return true; })
                .orElse(false);
    }

    @Override
    public User update(User user) {
        try {
            // Vérifier que l'utilisateur existe
            Optional<User> existingUser = userRepository.findById(user.getUserid());
            if (existingUser.isEmpty()) {
                throw new RuntimeException("Utilisateur non trouvé: " + user.getUserid());
            }

            // Préserver certains champs si non modifiés
            User existing = existingUser.get();
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(existing.getCreatedAt());
            }

            // Encoder le mot de passe seulement s'il a été modifié
            if (user.getPassword() != null && !user.getPassword().equals(existing.getPassword())
                    && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            User updatedUser = userRepository.save(user);
            log.info("User updated successfully: {}", updatedUser.getEmail());
            return updatedUser;

        } catch (Exception e) {
            log.error("Error updating user {}: {}", user.getUserid(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur", e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("Utilisateur non trouvé: " + id);
            }

            userRepository.deleteById(id);
            log.info("User deleted successfully: {}", id);

        } catch (Exception e) {
            log.error("Error deleting user {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Nouvelles méthodes pour le système de notifications

    @Override
    public List<User> findByRole(Role role) {
        try {
            return userRepository.findByRole(role);
        } catch (Exception e) {
            log.error("Error finding users by role {}: {}", role, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findByRoleAndLocation(Role role, String prefecture, String commune) {
        try {
            if (commune != null && !commune.trim().isEmpty()) {
                // Recherche par rôle, préfecture ET commune
                return userRepository.findByRoleAndPrefectureAndCommune(role, prefecture, commune);
            } else {
                // Recherche par rôle et préfecture seulement
                return userRepository.findByRoleAndPrefecture(role, prefecture);
            }
        } catch (Exception e) {
            log.error("Error finding users by role {} and location {}/{}: {}",
                    role, prefecture, commune, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findByPrefecture(String prefecture) {
        try {
            return userRepository.findByPrefecture(prefecture);
        } catch (Exception e) {
            log.error("Error finding users by prefecture {}: {}", prefecture, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findByPrefectureAndCommune(String prefecture, String commune) {
        try {
            return userRepository.findByPrefectureAndCommune(prefecture, commune);
        } catch (Exception e) {
            log.error("Error finding users by prefecture {} and commune {}: {}",
                    prefecture, commune, e.getMessage());
            return new ArrayList<>();
        }
    }

    // Méthode pour le UserController
    public List<User> findByLocation(String prefecture, String commune) {
        return findByPrefectureAndCommune(prefecture, commune);
    }

    @Override
    public List<User> findDSIMembers() {
        return findByRole(Role.MEMBRE_DSI);
    }

    @Override
    public Optional<User> findGovernorByPrefecture(String prefecture) {
        try {
            List<User> governors = userRepository.findByRoleAndPrefecture(Role.GOUVERNEUR, prefecture);
            return governors.isEmpty() ? Optional.empty() : Optional.of(governors.get(0));
        } catch (Exception e) {
            log.error("Error finding governor for prefecture {}: {}", prefecture, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAuthorityAgentsByLocation(String prefecture, String commune) {
        return findByRoleAndLocation(Role.AGENT_AUTORITE, prefecture, commune);
    }

    @Override
    public long countByRole(Role role) {
        try {
            return userRepository.countByRole(role);
        } catch (Exception e) {
            log.error("Error counting users by role {}: {}", role, e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean existsByRoleAndPrefecture(Role role, String prefecture) {
        try {
            return userRepository.existsByRoleAndPrefecture(role, prefecture);
        } catch (Exception e) {
            log.error("Error checking existence of users with role {} in prefecture {}: {}",
                    role, prefecture, e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Statistiques par rôle
            Map<String, Long> roleStats = new HashMap<>();
            for (Role role : Role.values()) {
                roleStats.put(role.name(), countByRole(role));
            }
            stats.put("byRole", roleStats);

            // Statistiques par préfecture
            List<User> allUsers = findAll();
            Map<String, Long> prefectureStats = allUsers.stream()
                    .filter(user -> user.getPrefecture() != null)
                    .collect(Collectors.groupingBy(User::getPrefecture, Collectors.counting()));
            stats.put("byPrefecture", prefectureStats);

            // Total
            stats.put("totalUsers", (long) allUsers.size());

            // Utilisateurs créés ce mois
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date startOfMonth = cal.getTime();

            long thisMonthCount = allUsers.stream()
                    .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().after(startOfMonth))
                    .count();
            stats.put("createdThisMonth", thisMonthCount);

            return stats;

        } catch (Exception e) {
            log.error("Error generating user statistics: {}", e.getMessage());
            stats.put("error", "Erreur lors du calcul des statistiques");
            return stats;
        }
    }

    // Méthodes utilitaires privées

    private String generateUserId() {
        return "USER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}