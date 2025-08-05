package com.example.HNR.Controller;

import com.example.HNR.Model.Mission;
import com.example.HNR.Service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = "*")
public class MissionController {

    @Autowired
    private MissionService missionService;

    // GET /api/missions - Obtenir toutes les missions
    @GetMapping
    public ResponseEntity<List<Mission>> getAllMissions() {
        List<Mission> missions = missionService.findAll();
        return ResponseEntity.ok(missions);
    }

    // GET /api/missions/{id} - Obtenir mission par ID
    @GetMapping("/{id}")
    public ResponseEntity<Mission> getMissionById(@PathVariable String id) {
        Optional<Mission> mission = missionService.findById(id);
        if (mission.isPresent()) {
            return ResponseEntity.ok(mission.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/missions/createur/{createurId} - Obtenir missions par créateur
    @GetMapping("/createur/{createurId}")
    public ResponseEntity<List<Mission>> getMissionsByCreateur(@PathVariable String createurId) {
        List<Mission> missions = missionService.findByCreateur(createurId);
        return ResponseEntity.ok(missions);
    }

    // GET /api/missions/prefecture/{prefecture} - Obtenir missions par préfecture
    @GetMapping("/prefecture/{prefecture}")
    public ResponseEntity<List<Mission>> getMissionsByPrefecture(@PathVariable String prefecture) {
        List<Mission> missions = missionService.findByPrefectureCommune(prefecture);
        return ResponseEntity.ok(missions);
    }

    // GET /api/missions/utilisateur/{utilisateurId} - Obtenir missions par utilisateur assigné
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Mission>> getMissionsByUtilisateur(@PathVariable String utilisateurId) {
        List<Mission> missions = missionService.findByUtilisateurAssigne(utilisateurId);
        return ResponseEntity.ok(missions);
    }

    // GET /api/missions/periode - Missions entre deux dates
    @GetMapping("/periode")
    public ResponseEntity<List<Mission>> getMissionsByPeriode(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Mission> missions = missionService.findByDateEnvoiBetween(startDate, endDate);
        return ResponseEntity.ok(missions);
    }

    // GET /api/missions/recentes - Missions récentes (30 derniers jours)
    @GetMapping("/recentes")
    public ResponseEntity<List<Mission>> getRecentMissions() {
        List<Mission> missions = missionService.findRecentMissions();
        return ResponseEntity.ok(missions);
    }

    // GET /api/missions/with-rapport - Missions avec rapport PDF
    @GetMapping("/with-rapport")
    public ResponseEntity<List<Mission>> getMissionsWithReport() {
        List<Mission> missions = missionService.findMissionsWithReport();
        return ResponseEntity.ok(missions);
    }

    // POST /api/missions - Créer nouvelle mission
    @PostMapping
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) {
        Mission createdMission = missionService.createMission(mission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMission);
    }

    // PUT /api/missions/{id} - Mettre à jour mission
    @PutMapping("/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable String id, @RequestBody Mission missionDetails) {
        try {
            Mission updatedMission = missionService.updateMission(id, missionDetails);
            return ResponseEntity.ok(updatedMission);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/missions/{id} - Supprimer mission
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable String id) {
        try {
            missionService.deleteMission(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/missions/{missionId}/assign/{userId} - Assigner utilisateur à mission
    @PostMapping("/{missionId}/assign/{userId}")
    public ResponseEntity<Void> assignUserToMission(@PathVariable String missionId, @PathVariable String userId) {
        try {
            missionService.assignUserToMission(missionId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/missions/{missionId}/assign/{userId} - Retirer utilisateur de mission
    @DeleteMapping("/{missionId}/assign/{userId}")
    public ResponseEntity<Void> removeUserFromMission(@PathVariable String missionId, @PathVariable String userId) {
        try {
            missionService.removeUserFromMission(missionId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/missions/count/statut/{statut} - Compter par statut
    @GetMapping("/count/statut/{statut}")
    public ResponseEntity<Long> countMissionsByStatut(@PathVariable String statut) {
        long count = missionService.countByStatut(statut);
        return ResponseEntity.ok(count);
    }
}