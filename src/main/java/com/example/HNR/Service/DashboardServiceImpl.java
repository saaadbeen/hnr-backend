package com.example.HNR.Service;

import com.example.HNR.DTO.*;
import com.example.HNR.Model.enums.StatutDouar;
import com.example.HNR.Model.enums.TypeAction;
import com.example.HNR.Model.enums.TypeExtension;
import com.example.HNR.Repository.SqlServer.ActionRepository;
import com.example.HNR.Repository.SqlServer.DouarRepository;
import com.example.HNR.Repository.SqlServer.MissionRepository;
import com.example.HNR.Repository.SqlServer.PVRepository;
import com.example.HNR.Repository.SqlServer.ChangementRepository;
import com.example.HNR.Repository.Mongodb.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DouarRepository douarRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ChangementRepository changementRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private PVRepository pvRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        return DashboardStatsDTO.builder()
                // Statistiques des douars
                .totalDouars(douarRepository.count())
                .douarsEradiques(douarRepository.countByStatut(StatutDouar.ERADIQUE))
                .douarsNonEradiques(douarRepository.countByStatut(StatutDouar.NON_ERADIQUE))
                .pourcentageEradication(calculateEradicationPercentage())

                // Statistiques des actions
                .totalActions(actionRepository.count())
                .totalDemolitions(actionRepository.countByType(TypeAction.DEMOLITION))
                .totalSignalements(actionRepository.countByType(TypeAction.SIGNALEMENT))
                .totalNonDemolitions(actionRepository.countByType(TypeAction.NON_DEMOLITION))

                // Statistiques des changements
                .totalChangements(changementRepository.count())
                .extensionsHorizontales(changementRepository.countByType(TypeExtension.HORIZONTAL))
                .extensionsVerticales(changementRepository.countByType(TypeExtension.VERTICAL))
                .surfaceTotaleExtensions(calculateTotalSurfaceExtensions())

                // Statistiques des missions
                .totalMissions(missionRepository.count())
                .missionsTerminees(missionRepository.countByStatut("TERMINEE"))
                .missionsEnCours(missionRepository.count() - missionRepository.countByStatut("TERMINEE"))

                // Statistiques des PV
                .totalPVs(pvRepository.count())
                .pvsValides(pvRepository.countByValide(true))
                .pvsEnAttente(pvRepository.countByValide(false))

                // Statistiques des utilisateurs
                .totalUtilisateurs(userRepository.count())
                .agentsAutorite(userRepository.countByRole(com.example.HNR.Model.enums.Role.AGENT_AUTORITE))
                .membresDSI(userRepository.countByRole(com.example.HNR.Model.enums.Role.MEMBRE_DSI))
                .gouverneurs(userRepository.countByRole(com.example.HNR.Model.enums.Role.GOUVERNEUR))

                // Données pour graphiques
                .actionsByType(getActionsByType())
                .changementsByType(getChangementsByType())
                .douarsByStatut(getDouarsByStatut())
                .missionsByStatut(getMissionsByStatut())
                .actionsByMonth(getActionsByMonth())
                .changementsByMonth(getChangementsByMonth())
                .actionsByPrefecture(getActionsByPrefecture())
                .douarsByPrefecture(getDouarsByPrefecture())

                // Top données
                .build();
    }

    @Override
    public DashboardStatsDTO getDashboardStatsByPrefecture(String prefecture) {
        // Implémentation similaire mais filtrée par préfecture
        return getDashboardStats(); // Simplifiée pour l'exemple
    }

    @Override
    public DashboardStatsDTO getDashboardStatsByDateRange(Date startDate, Date endDate) {
        // Implémentation avec filtrage par dates
        return getDashboardStats(); // Simplifiée pour l'exemple
    }

    @Override
    public List<PrefectureStatsDTO> getPrefectureStats() {
        // Récupérer toutes les préfectures distinctes
        List<String> prefectures = douarRepository.findDistinctPrefectures();

        return prefectures.stream().map(prefecture -> {
            long totalDouars = douarRepository.countByPrefecture(prefecture);
            long douarsEradiques = douarRepository.countByPrefectureAndStatut(prefecture, StatutDouar.ERADIQUE);
            long totalActions = actionRepository.countByPrefecture(prefecture);
            long totalChangements = changementRepository.countByDouarPrefecture(prefecture);

            return new PrefectureStatsDTO(
                    prefecture,
                    totalDouars,
                    douarsEradiques,
                    totalActions,
                    totalChangements,
                    totalDouars > 0 ? (double) douarsEradiques / totalDouars * 100 : 0
            );
        }).collect(Collectors.toList());
    }

    // Méthodes utilitaires privées
    private double calculateEradicationPercentage() {
        long total = douarRepository.count();
        long eradiques = douarRepository.countByStatut(StatutDouar.ERADIQUE);
        return total > 0 ? (double) eradiques / total * 100 : 0;
    }

    private double calculateTotalSurfaceExtensions() {
        return changementRepository.sumSurface().orElse(0.0);
    }

    private Map<String, Long> getActionsByType() {
        Map<String, Long> result = new HashMap<>();
        result.put("DEMOLITION", actionRepository.countByType(TypeAction.DEMOLITION));
        result.put("SIGNALEMENT", actionRepository.countByType(TypeAction.SIGNALEMENT));
        result.put("NON_DEMOLITION", actionRepository.countByType(TypeAction.NON_DEMOLITION));
        return result;
    }

    private Map<String, Long> getChangementsByType() {
        Map<String, Long> result = new HashMap<>();
        result.put("HORIZONTAL", changementRepository.countByType(TypeExtension.HORIZONTAL));
        result.put("VERTICAL", changementRepository.countByType(TypeExtension.VERTICAL));
        return result;
    }

    private Map<String, Long> getDouarsByStatut() {
        Map<String, Long> result = new HashMap<>();
        result.put("ERADIQUE", douarRepository.countByStatut(StatutDouar.ERADIQUE));
        result.put("NON_ERADIQUE", douarRepository.countByStatut(StatutDouar.NON_ERADIQUE));
        return result;
    }

    private Map<String, Long> getMissionsByStatut() {
        Map<String, Long> result = new HashMap<>();
        result.put("TERMINEE", missionRepository.countByStatut("TERMINEE"));
        result.put("EN_COURS", missionRepository.count() - missionRepository.countByStatut("TERMINEE"));
        return result;
    }

    private Map<String, Long> getActionsByMonth() {
        // Implémentation pour les 12 derniers mois
        return new HashMap<>(); // Simplifiée
    }

    private Map<String, Long> getChangementsByMonth() {
        // Implémentation pour les 12 derniers mois
        return new HashMap<>(); // Simplifiée
    }

    private Map<String, Long> getActionsByPrefecture() {
        return actionRepository.countByPrefectureGrouped()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }

    private Map<String, Long> getDouarsByPrefecture() {
        return douarRepository.countByPrefectureGrouped()
                .stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }
}
