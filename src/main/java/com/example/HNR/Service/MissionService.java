package com.example.HNR.Service;

import com.example.HNR.Model.Mission;
import com.example.HNR.Repository.MissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class MissionService {

    private final MissionRepository missionRepository;

    public MissionService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    public Optional<Mission> getMissionById(String id) {
        return missionRepository.findById(id);
    }

    public Mission createMission(Mission mission) {
        return missionRepository.save(mission);
    }

    public void deleteMission(String id) {
        missionRepository.deleteById(id);
    }

    public Mission updateMission(String id, Mission updated) {
        return missionRepository.findById(id).map(mission -> {
            mission.setNom(updated.getNom());
            mission.setRapportUrl(updated.getRapportUrl());
            mission.setDateEnvoi(updated.getDateEnvoi());
            mission.setPrefecture(updated.getPrefecture());
            mission.setCommune(updated.getCommune());
            mission.setCreateurId(updated.getCreateurId());
            mission.setDouars(updated.getDouars());
            return missionRepository.save(mission);
        }).orElse(null);
    }

    public List<Mission> getByPrefecture(String prefecture) {
        return missionRepository.findByPrefecture(prefecture);
    }

    public List<Mission> getByCommune(String commune) {
        return missionRepository.findByCommune(commune);
    }

    public List<Mission> getByCreateurId(String userId) {
        return missionRepository.findByCreateurId(userId);
    }

    public List<Mission> searchByNom(String nom) {
        return missionRepository.findByNomContainingIgnoreCase(nom);
    }
}