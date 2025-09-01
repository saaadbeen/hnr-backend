package com.example.HNR.Controller;

import com.example.HNR.Model.SqlServer.Mission;
import com.example.HNR.Service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    @Autowired
    private MissionService missionService;

    // GET all missions - accessible à tous les rôles authentifiés
    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Page<Mission>> getAllMissions(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Page<Mission> missions = missionService.findAll(PageRequest.of(page, size));
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // GET mission by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
        Optional<Mission> mission = missionService.findById(id);
        if (mission.isPresent()) {
            return new ResponseEntity<>(mission.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST create mission - accessible aux GOUVERNEUR et MEMBRE_DSI
    @PostMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) {
        // Validation basique
        if (mission.getPrefecture() == null || mission.getCommune() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (mission.getDateEnvoi() == null) {
            mission.setDateEnvoi(new Date());
        }
        if (mission.getStatut() == null || mission.getStatut().trim().isEmpty()) {
            mission.setStatut("EN_COURS"); // Statut par défaut
        }

        Mission savedMission = missionService.create(mission);
        return new ResponseEntity<>(savedMission, HttpStatus.CREATED);
    }

    // PUT update mission - créateur + GOUVERNEUR/MEMBRE_DSI
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@missionServiceImpl.findById(#id).isPresent() and @missionServiceImpl.findById(#id).get().getCreatedByUserId() == authentication.name)")
    public ResponseEntity<Mission> updateMission(@PathVariable Long id, @RequestBody Mission mission) {
        Optional<Mission> existingMission = missionService.findById(id);
        if (existingMission.isPresent()) {
            mission.setMissionId(id);
            // Conserver les métadonnées originales
            mission.setCreatedAt(existingMission.get().getCreatedAt());
            // Conserver la date de completion si elle existe déjà
            if (existingMission.get().getCompletedAt() != null) {
                mission.setCompletedAt(existingMission.get().getCompletedAt());
            }

            Mission updatedMission = missionService.update(mission);
            return new ResponseEntity<>(updatedMission, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE mission - accessible aux GOUVERNEUR uniquement
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR')")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        Optional<Mission> mission = missionService.findById(id);
        if (mission.isPresent()) {
            missionService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET missions by statut
    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Mission>> getMissionsByStatut(@PathVariable String statut) {
        List<Mission> missions = missionService.findByStatut(statut);
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // GET missions by location
    @GetMapping("/location")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Mission>> getMissionsByLocation(
            @RequestParam String prefecture,
            @RequestParam String commune) {
        List<Mission> missions = missionService.findByLocation(prefecture, commune);
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // GET missions by creator user ID
    @GetMapping("/created-by/{userId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #userId == authentication.name")
    public ResponseEntity<List<Mission>> getMissionsByCreator(@PathVariable String userId) {
        List<Mission> missions = missionService.findByCreatedByUserId(userId);
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // GET missions by date range
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Mission>> getMissionsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Mission> missions = missionService.findByDateRange(startDate, endDate);
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // GET completed missions
    @GetMapping("/completed")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Mission>> getCompletedMissions() {
        List<Mission> missions = missionService.findCompletedMissions();
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // GET active missions (non terminées)
    @GetMapping("/active")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Mission>> getActiveMissions() {
        List<Mission> missions = missionService.findActiveMissions();
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // GET missions by current user (créées par l'utilisateur connecté)
    @GetMapping("/my-missions")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Mission>> getMyMissions() {
        String currentUserId = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        List<Mission> missions = missionService.findByCreatedByUserId(currentUserId);
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // GET missions assigned to current user
    @GetMapping("/assigned-to-me")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Mission>> getMissionsAssignedToMe() {
        String currentUserId = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        // Récupérer toutes les missions et filtrer celles assignées à l'utilisateur connecté
        List<Mission> allMissions = missionService.findAll(Pageable.unpaged()).getContent();
        List<Mission> assignedMissions = allMissions.stream()
                .filter(mission -> mission.getAssignedUserIds() != null &&
                        mission.getAssignedUserIds().contains(currentUserId))
                .toList();

        return new ResponseEntity<>(assignedMissions, HttpStatus.OK);
    }

    // PUT complete mission - créateur + GOUVERNEUR/MEMBRE_DSI
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@missionServiceImpl.findById(#id).isPresent() and @missionServiceImpl.findById(#id).get().getCreatedByUserId() == authentication.name)")
    public ResponseEntity<Mission> completeMission(@PathVariable Long id) {
        Optional<Mission> existingMission = missionService.findById(id);
        if (existingMission.isPresent()) {
            if (existingMission.get().isCompleted()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT); // Déjà terminée
            }

            missionService.completeMission(id);
            // Récupérer la mission mise à jour
            Optional<Mission> updatedMission = missionService.findById(id);
            return new ResponseEntity<>(updatedMission.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT update mission report
    @PutMapping("/{id}/report")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@missionServiceImpl.findById(#id).isPresent() and @missionServiceImpl.findById(#id).get().getCreatedByUserId() == authentication.name)")
    public ResponseEntity<Mission> updateMissionReport(
            @PathVariable Long id,
            @RequestBody ReportUpdateRequest request) {
        Optional<Mission> existingMission = missionService.findById(id);
        if (existingMission.isPresent()) {
            Mission mission = existingMission.get();
            mission.setRapportPDF(request.getRapportPDF());

            Mission updatedMission = missionService.update(mission);
            return new ResponseEntity<>(updatedMission, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT assign users to mission
    @PutMapping("/{id}/assign-users")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Mission> assignUsersToMission(
            @PathVariable Long id,
            @RequestBody UserAssignmentRequest request) {
        Optional<Mission> existingMission = missionService.findById(id);
        if (existingMission.isPresent()) {
            Mission mission = existingMission.get();
            mission.setAssignedUserIds(request.getUserIds());

            Mission updatedMission = missionService.update(mission);
            return new ResponseEntity<>(updatedMission, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET missions en cours (non terminées)
    @GetMapping("/en-cours")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Mission>> getMissionsEnCours() {
        List<Mission> missions = missionService.findByStatut("EN_COURS");
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // GET missions terminées
    @GetMapping("/terminees")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Mission>> getMissionsTerminees() {
        List<Mission> missions = missionService.findByStatut("TERMINEE");
        return new ResponseEntity<>(missions, HttpStatus.OK);
    }

    // Classes internes pour les requêtes
    public static class ReportUpdateRequest {
        private String rapportPDF;

        public ReportUpdateRequest() {}
        public ReportUpdateRequest(String rapportPDF) {
            this.rapportPDF = rapportPDF;
        }

        public String getRapportPDF() { return rapportPDF; }
        public void setRapportPDF(String rapportPDF) { this.rapportPDF = rapportPDF; }
    }

    public static class UserAssignmentRequest {
        private List<String> userIds;

        public UserAssignmentRequest() {}
        public UserAssignmentRequest(List<String> userIds) {
            this.userIds = userIds;
        }

        public List<String> getUserIds() { return userIds; }
        public void setUserIds(List<String> userIds) { this.userIds = userIds; }
    }
}