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
@org.springframework.context.annotation.Profile("!dev")
public class DashboardServiceImpl implements DashboardService {

    @Autowired private DouarRepository douarRepository;
    @Autowired private ActionRepository actionRepository;
    @Autowired private ChangementRepository changementRepository;
    @Autowired private MissionRepository missionRepository;
    @Autowired private PVRepository pvRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        return DashboardStatsDTO.builder()
                // Douars
                .totalDouars(douarRepository.count())
                .douarsEradiques(douarRepository.countByStatut(StatutDouar.ERADIQUE))
                .douarsNonEradiques(douarRepository.countByStatut(StatutDouar.NON_ERADIQUE))
                .pourcentageEradication(calculateEradicationPercentage())

                // Actions
                .totalActions(actionRepository.count())
                .totalDemolitions(actionRepository.countByType(TypeAction.DEMOLITION))
                .totalSignalements(actionRepository.countByType(TypeAction.SIGNALEMENT))
                .totalNonDemolitions(actionRepository.countByType(TypeAction.NON_DEMOLITION))

                // Changements
                .totalChangements(changementRepository.count())

                // Missions
                .totalMissions(missionRepository.count())
                .missionsTerminees(missionRepository.countByStatut("TERMINEE"))
                .missionsEnCours(missionRepository.count() - missionRepository.countByStatut("TERMINEE"))

                // PVs
                .totalPVs(pvRepository.count())


                // Utilisateurs
                .totalUtilisateurs(userRepository.count())
                .agentsAutorite(userRepository.countByRole(com.example.HNR.Model.enums.Role.AGENT_AUTORITE))
                .membresDSI(userRepository.countByRole(com.example.HNR.Model.enums.Role.MEMBRE_DSI))
                .gouverneurs(userRepository.countByRole(com.example.HNR.Model.enums.Role.GOUVERNEUR))

                // Graphs
                .actionsByType(getActionsByType())
                .changementsByType(getChangementsByType())
                .douarsByStatut(getDouarsByStatut())
                .missionsByStatut(getMissionsByStatut())
                .actionsByMonth(getActionsByMonth())
                .changementsByMonth(getChangementsByMonth())
                .actionsByPrefecture(getActionsByPrefecture())
                .douarsByPrefecture(getDouarsByPrefecture())

                .build();
    }

    @Override
    public DashboardStatsDTO getDashboardStatsByPrefecture(String prefecture) {
        // à affiner si besoin (filtrage par préfecture)
        return getDashboardStats();
    }

    @Override
    public DashboardStatsDTO getDashboardStatsByDateRange(Date startDate, Date endDate) {
        // à affiner si besoin (filtrage par dates)
        return getDashboardStats();
    }

    @Override
    public List<PrefectureStatsDTO> getPrefectureStats() {
        List<String> prefectures = douarRepository.findDistinctPrefectures();

        return prefectures.stream().map(prefecture -> {
            long totalDouars       = douarRepository.countByPrefecture(prefecture);
            long douarsEradiques   = douarRepository.countByPrefectureAndStatut(prefecture, StatutDouar.ERADIQUE);
            long totalActions      = actionRepository.countByPrefecture(prefecture);
            long totalChangements  = changementRepository.countByPrefecture(prefecture); // ✅ défini ici

            return new PrefectureStatsDTO(
                    prefecture,
                    totalDouars,
                    douarsEradiques,
                    totalActions,
                    totalChangements, // ✅ variable existe
                    totalDouars > 0 ? (double) douarsEradiques / totalDouars * 100 : 0
            );
        }).collect(Collectors.toList());
    }

    private double calculateEradicationPercentage() {
        long total = douarRepository.count();
        long eradiques = douarRepository.countByStatut(StatutDouar.ERADIQUE);
        return total > 0 ? (double) eradiques / total * 100 : 0;
    }

    private Map<String, Long> getActionsByType() {
        Map<String, Long> result = new HashMap<>();
        result.put("DEMOLITION",   actionRepository.countByType(TypeAction.DEMOLITION));
        result.put("SIGNALEMENT",  actionRepository.countByType(TypeAction.SIGNALEMENT));
        result.put("NON_DEMOLITION", actionRepository.countByType(TypeAction.NON_DEMOLITION));
        return result;
    }

    private Map<String, Long> getChangementsByType() {
        Map<String, Long> result = new HashMap<>();
        result.put("HORIZONTAL", changementRepository.countByType(TypeExtension.HORIZONTAL)); // ✅ méthode ajoutée
        result.put("VERTICAL",   changementRepository.countByType(TypeExtension.VERTICAL));   // ✅ méthode ajoutée
        return result;
    }

    private Map<String, Long> getDouarsByStatut() {
        Map<String, Long> result = new HashMap<>();
        result.put("ERADIQUE",      douarRepository.countByStatut(StatutDouar.ERADIQUE));
        result.put("NON_ERADIQUE",  douarRepository.countByStatut(StatutDouar.NON_ERADIQUE));
        return result;
    }

    private Map<String, Long> getMissionsByStatut() {
        Map<String, Long> result = new HashMap<>();
        result.put("TERMINEE", missionRepository.countByStatut("TERMINEE"));
        result.put("EN_COURS", missionRepository.count() - missionRepository.countByStatut("TERMINEE"));
        return result;
    }

    private Map<String, Long> getActionsByMonth() { return new HashMap<>(); }
    private Map<String, Long> getChangementsByMonth() { return new HashMap<>(); }

    private Map<String, Long> getActionsByPrefecture() {
        return actionRepository.countByPrefectureGrouped()
                .stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1]
                ));
    }

    private Map<String, Long> getDouarsByPrefecture() {
        return douarRepository.countByPrefectureGrouped()
                .stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1]
                ));
    }
}
