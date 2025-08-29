package com.example.HNR.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    // Statistiques générales
    private long totalDouars;
    private long douarsEradiques;
    private long douarsNonEradiques;
    private double pourcentageEradication;

    private long totalActions;
    private long totalDemolitions;
    private long totalSignalements;
    private long totalNonDemolitions;

    private long totalChangements;
    private long extensionsHorizontales;
    private long extensionsVerticales;
    private double surfaceTotaleExtensions;

    private long totalMissions;
    private long missionsTerminees;
    private long missionsEnCours;

    private long totalPVs;
    private long pvsValides;
    private long pvsEnAttente;

    private long totalUtilisateurs;
    private long agentsAutorite;
    private long membresDSI;
    private long gouverneurs;

    // Données pour graphiques
    private Map<String, Long> actionsByType;
    private Map<String, Long> changementsByType;
    private Map<String, Long> douarsByStatut;
    private Map<String, Long> missionsByStatut;
    private Map<String, Long> actionsByMonth;
    private Map<String, Long> changementsByMonth;
    private Map<String, Long> actionsByPrefecture;
    private Map<String, Long> douarsByPrefecture;
}