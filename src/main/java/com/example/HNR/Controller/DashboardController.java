/**package com.example.HNR.Controller;

import com.example.HNR.Model.StatutDouar;
import com.example.HNR.Model.Role;
import com.example.HNR.Model.TypeAction;
import com.example.HNR.Model.TypeExtension;
import com.example.HNR.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private DouarService douarService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ChangementService changementService;

    @Autowired
    private PVService pvService;

    // GET /api/dashboard/stats - Statistiques générales du dashboard
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques utilisateurs
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("total", userService.findAll().size());
        userStats.put("agents", userService.countByRole(Role.AGENT_AUTORITE));
        userStats.put("dsi", userService.countByRole(Role.MEMBRE_DSI));
        userStats.put("gouverneurs", userService.countByRole(Role.GOUVERNEUR));
        userStats.put("admins", userService.countByRole(Role.Admin));
        stats.put("users", userStats);

        // Statistiques douars
        Map<String, Object> douarStats = new HashMap<>();
        douarStats.put("total", douarService.findAll().size());
        douarStats.put("eradiques", douarService.countByStatut(StatutDouar.ERADIQUE));
        douarStats.put("nonEradiques", douarService.countByStatut(StatutDouar.NON_ERADIQUE));
        douarStats.put("withActions", douarService.findDouarsWithActions().size());
        stats.put("douars", douarStats);

        // Statistiques missions
        Map<String, Object> missionStats = new HashMap<>();
        missionStats.put("total", missionService.findAll().size());
        missionStats.put("recentes", missionService.findRecentMissions().size());
        missionStats.put("withRapport", missionService.findMissionsWithReport().size());
        stats.put("missions", missionStats);

        // Statistiques actions
        Map<String, Object> actionStats = new HashMap<>();
        actionStats.put("total", actionService.findAll().size());
        actionStats.put("demolitions", actionService.findByType(TypeAction.DEMOLITION).size());
        actionStats.put("signalements", actionService.findByType(TypeAction.SIGNALEMENT).size());
        actionStats.put("nonDemolitions", actionService.findByType(TypeAction.NON_DEMOLITION).size());
        actionStats.put("withAvis", actionService.findActionsWithAvisPrefecture().size());
        stats.put("actions", actionStats);

        // Statistiques changements
        Map<String, Object> changementStats = new HashMap<>();
        changementStats.put("total", changementService.findAll().size());
        changementStats.put("horizontal", changementService.countByType(TypeExtension.HORIZONTAL));
        changementStats.put("vertical", changementService.countByType(TypeExtension.VERTICAL));
        changementStats.put("withPhotos", changementService.findChangementsWithBothPhotos().size());
        stats.put("changements", changementStats);

        // Statistiques PVs
        Map<String, Object> pvStats = new HashMap<>();
        pvStats.put("total", pvService.findAll().size());
        pvStats.put("valides", pvService.countByValide(true));
        pvStats.put("enAttente", pvService.countByValide(false));
        stats.put("pvs", pvStats);

        return ResponseEntity.ok(stats);
    }

    // GET /api/dashboard/stats/prefecture/{prefecture} - Statistiques par préfecture
    @GetMapping("/stats/prefecture/{prefecture}")
    public ResponseEntity<Map<String, Object>> getStatsByPrefecture(@PathVariable String prefecture) {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques par préfecture
        stats.put("users", userService.findByPrefectureCommune(prefecture).size());
        stats.put("douars", douarService.countByPrefectureCommune(prefecture));
        stats.put("douarsEradiques", douarService.findByStatutAndPrefectureCommune(StatutDouar.ERADIQUE, prefecture).size());
        stats.put("douarsNonEradiques", douarService.findByStatutAndPrefectureCommune(StatutDouar.NON_ERADIQUE, prefecture).size());
        stats.put("missions", missionService.findByPrefectureCommune(prefecture).size());
        stats.put("actions", actionService.findByPrefectureCommune(prefecture).size());

        return ResponseEntity.ok(stats);
    }
}*/