package com.example.HNR.Service;

import com.example.HNR.Model.enums.Role;
import com.example.HNR.Service.NotificationAudienceResolver;
import com.example.HNR.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationAudienceResolverImpl implements NotificationAudienceResolver {

    private final UserService userService;

    @Override
    public List<String> resolveMissionCreatedAudience(Long missionId, String creatorUserId,
                                                      List<String> assignedUserIds,
                                                      String prefecture, String commune) {
        Set<String> audience = new HashSet<>();

        // Tous les utilisateurs assignés à la mission
        if (assignedUserIds != null) {
            audience.addAll(assignedUserIds);
        }

        // Tous les membres DSI (supervision globale)
        audience.addAll(getDSIMembers());

        // Le gouverneur de la préfecture concernée
        String governor = getGovernorForPrefecture(prefecture);
        if (governor != null) {
            audience.add(governor);
        }

        // Agents d'autorité dans la zone concernée (pour info)
        audience.addAll(getAuthorityAgentsForLocation(prefecture, commune));

        // Exclure le créateur pour éviter l'auto-notification
        audience.remove(creatorUserId);

        log.debug("Mission created audience for mission {}: {} users", missionId, audience.size());
        return new ArrayList<>(audience);
    }

    @Override
    public List<String> resolveMissionAssignedAudience(Long missionId, String assignerUserId,
                                                       List<String> assignedUserIds) {
        Set<String> audience = new HashSet<>();

        // Les utilisateurs nouvellement assignés
        if (assignedUserIds != null) {
            audience.addAll(assignedUserIds);
        }

        // Membres DSI pour supervision
        audience.addAll(getDSIMembers());

        // Exclure celui qui fait l'assignation
        audience.remove(assignerUserId);

        log.debug("Mission assigned audience for mission {}: {} users", missionId, audience.size());
        return new ArrayList<>(audience);
    }

    @Override
    public List<String> resolveActionCreatedAudience(Long actionId, String actionUserId,
                                                     String prefecture, String commune) {
        Set<String> audience = new HashSet<>();

        // Membres DSI (supervision de toutes les actions)
        audience.addAll(getDSIMembers());

        // Gouverneur de la préfecture
        String governor = getGovernorForPrefecture(prefecture);
        if (governor != null) {
            audience.add(governor);
        }

        // Autres agents d'autorité dans la même zone (pour coordination)
        List<String> localAgents = getAuthorityAgentsForLocation(prefecture, commune);
        localAgents.remove(actionUserId); // Exclure l'auteur de l'action
        audience.addAll(localAgents);

        log.debug("Action created audience for action {}: {} users", actionId, audience.size());
        return new ArrayList<>(audience);
    }

    @Override
    public List<String> resolveActionCompletedAudience(Long actionId, String actionUserId,
                                                       String prefecture, String commune) {
        Set<String> audience = new HashSet<>();

        // Même logique que la création, mais avec un message différent
        audience.addAll(getDSIMembers());

        String governor = getGovernorForPrefecture(prefecture);
        if (governor != null) {
            audience.add(governor);
        }

        List<String> localAgents = getAuthorityAgentsForLocation(prefecture, commune);
        localAgents.remove(actionUserId);
        audience.addAll(localAgents);

        log.debug("Action completed audience for action {}: {} users", actionId, audience.size());
        return new ArrayList<>(audience);
    }

    @Override
    public List<String> resolveChangementDeclaredAudience(Long changementId, String detectorUserId,
                                                          String prefecture, String commune) {
        Set<String> audience = new HashSet<>();

        // DSI pour analyse des changements
        audience.addAll(getDSIMembers());

        // Gouverneur pour validation/action
        String governor = getGovernorForPrefecture(prefecture);
        if (governor != null) {
            audience.add(governor);
        }

        // Agents d'autorité locaux pour intervention éventuelle
        audience.addAll(getAuthorityAgentsForLocation(prefecture, commune));

        // Exclure le détecteur
        audience.remove(detectorUserId);

        log.debug("Changement declared audience for changement {}: {} users", changementId, audience.size());
        return new ArrayList<>(audience);
    }

    @Override
    public List<String> resolvePVGeneratedAudience(Long pvId, Long actionId,
                                                   String prefecture, String commune) {
        Set<String> audience = new HashSet<>();

        // DSI pour archivage et suivi
        audience.addAll(getDSIMembers());

        // Gouverneur pour validation officielle
        String governor = getGovernorForPrefecture(prefecture);
        if (governor != null) {
            audience.add(governor);
        }

        // Tous les agents d'autorité de la zone (le PV les concerne)
        audience.addAll(getAuthorityAgentsForLocation(prefecture, commune));

        log.debug("PV generated audience for PV {}: {} users", pvId, audience.size());
        return new ArrayList<>(audience);
    }

    @Override
    public List<String> resolveUserCreatedAudience(String newUserId, String creatorUserId,
                                                   Role newUserRole, String prefecture) {
        Set<String> audience = new HashSet<>();

        // Tous les membres DSI (gestion des utilisateurs)
        audience.addAll(getDSIMembers());

        // Si c'est un gouverneur qui est créé, notifier les autres gouverneurs
        if (newUserRole == Role.GOUVERNEUR) {
            audience.addAll(getUsersByRole(Role.GOUVERNEUR));
        }

        // Le gouverneur de la préfecture concernée (nouveau collègue)
        if (newUserRole == Role.AGENT_AUTORITE) {
            String governor = getGovernorForPrefecture(prefecture);
            if (governor != null) {
                audience.add(governor);
            }
        }

        // Exclure le créateur et le nouvel utilisateur lui-même
        audience.remove(creatorUserId);
        audience.remove(newUserId);

        log.debug("User created audience for new user {}: {} users", newUserId, audience.size());
        return new ArrayList<>(audience);
    }

    @Override
    public List<String> getUsersByRole(Role role) {
        try {
            return userService.findByRole(role)
                    .stream()
                    .map(user -> user.getUserid())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting users by role {}: {}", role, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getUsersByRoleAndLocation(Role role, String prefecture, String commune) {
        try {
            return userService.findByRoleAndLocation(role, prefecture, commune)
                    .stream()
                    .map(user -> user.getUserid())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting users by role {} and location {}/{}: {}",
                    role, prefecture, commune, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getDSIMembers() {
        return getUsersByRole(Role.MEMBRE_DSI);
    }

    @Override
    public String getGovernorForPrefecture(String prefecture) {
        if (prefecture == null || prefecture.trim().isEmpty()) {
            return null;
        }

        List<String> governors = getUsersByRoleAndLocation(Role.GOUVERNEUR, prefecture, null);
        return governors.isEmpty() ? null : governors.get(0);
    }

    @Override
    public List<String> getAuthorityAgentsForLocation(String prefecture, String commune) {
        return getUsersByRoleAndLocation(Role.AGENT_AUTORITE, prefecture, commune);
    }

    @Override
    public List<String> resolveCustomAudience(Map<String, Object> criteria) {
        Set<String> audience = new HashSet<>();

        try {
            // Critère par rôle
            if (criteria.containsKey("role")) {
                Role role = (Role) criteria.get("role");
                String prefecture = (String) criteria.get("prefecture");
                String commune = (String) criteria.get("commune");

                if (prefecture != null) {
                    audience.addAll(getUsersByRoleAndLocation(role, prefecture, commune));
                } else {
                    audience.addAll(getUsersByRole(role));
                }
            }

            // Critère par IDs spécifiques
            if (criteria.containsKey("userIds")) {
                @SuppressWarnings("unchecked")
                List<String> userIds = (List<String>) criteria.get("userIds");
                audience.addAll(userIds);
            }

            // Exclusions
            if (criteria.containsKey("exclude")) {
                @SuppressWarnings("unchecked")
                List<String> excludeIds = (List<String>) criteria.get("exclude");
                audience.removeAll(excludeIds);
            }

            // Inclure DSI par défaut ?
            if (Boolean.TRUE.equals(criteria.get("includeDSI"))) {
                audience.addAll(getDSIMembers());
            }

        } catch (Exception e) {
            log.error("Error resolving custom audience: {}", e.getMessage());
        }

        return new ArrayList<>(audience);
    }
}