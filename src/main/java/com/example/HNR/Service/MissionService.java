package com.example.HNR.Service;

import com.example.HNR.Model.Mission;
import com.example.HNR.Repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Calendar;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    // Créer une mission
    public Mission createMission(Mission mission) {
        mission.setDateCreation(new Date());
        return missionRepository.save(mission);
    }

    // Trouver par ID
    public Optional<Mission> findById(String id) {
        return missionRepository.findById(id);
    }

    // Obtenir toutes les missions
    public List<Mission> findAll() {
        return missionRepository.findAll();
    }

    // Trouver par créateur
    public List<Mission> findByCreateur(String createurId) {
        return missionRepository.findByCreePar(createurId);
    }

    // Trouver par préfecture
    public List<Mission> findByPrefectureCommune(String prefectureCommune) {
        return missionRepository.findByPrefectureCommune(prefectureCommune);
    }

    // Trouver par utilisateur assigné
    public List<Mission> findByUtilisateurAssigne(String utilisateurId) {
        return missionRepository.findByUtilisateurAssigne(utilisateurId);
    }

    // Missions envoyées entre deux dates
    public List<Mission> findByDateEnvoiBetween(Date startDate, Date endDate) {
        return missionRepository.findByDateEnvoiBetween(startDate, endDate);
    }

    // Missions récentes (30 derniers jours)
    public List<Mission> findRecentMissions() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        return missionRepository.findRecentMissions(cal.getTime());
    }

    // Missions avec rapport
    public List<Mission> findMissionsWithReport() {
        return missionRepository.findMissionsWithReport();
    }

    // Mettre à jour une mission
    public Mission updateMission(String id, Mission missionDetails) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée avec ID: " + id));

        if (missionDetails.getDateEnvoi() != null) {
            mission.setDateEnvoi(missionDetails.getDateEnvoi());
        }
        if (missionDetails.getPrefectureCommune() != null) {
            mission.setPrefectureCommune(missionDetails.getPrefectureCommune());
        }
        if (missionDetails.getRapportPDF() != null) {
            mission.setRapportPDF(missionDetails.getRapportPDF());
        }
        if (missionDetails.getUtilisateursAssignes() != null) {
            mission.setUtilisateursAssignes(missionDetails.getUtilisateursAssignes());
        }
        if (missionDetails.getStatut() != null) {
            mission.setStatut(missionDetails.getStatut());
        }
        if (missionDetails.getNombreDouars() > 0) {
            mission.setNombreDouars(missionDetails.getNombreDouars());
        }
        if (missionDetails.getNombreActions() > 0) {
            mission.setNombreActions(missionDetails.getNombreActions());
        }

        return missionRepository.save(mission);
    }

    // Supprimer une mission
    public void deleteMission(String id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée avec ID: " + id));
        missionRepository.delete(mission);
    }

    // Compter par statut
    public long countByStatut(String statut) {
        return missionRepository.countByStatut(statut);
    }

    // Assigner un utilisateur à une mission
    public void assignUserToMission(String missionId, String userId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        if (!mission.getUtilisateursAssignes().contains(userId)) {
            mission.getUtilisateursAssignes().add(userId);
            missionRepository.save(mission);
        }
    }

    // Retirer un utilisateur d'une mission
    public void removeUserFromMission(String missionId, String userId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        mission.getUtilisateursAssignes().remove(userId);
        missionRepository.save(mission);
    }
}