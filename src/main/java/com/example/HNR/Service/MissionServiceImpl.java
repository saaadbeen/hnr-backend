package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Mission;
import com.example.HNR.Repository.SqlServer.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MissionServiceImpl implements MissionService {

    @Autowired
    private MissionRepository missionRepository;

    @Override
    public Mission create(Mission mission) {
        return missionRepository.save(mission);
    }

    @Override
    public Optional<Mission> findById(Long id) {
        return missionRepository.findById(id);
    }

    @Override
    public List<Mission> findAll() {
        return missionRepository.findAll();
    }

    @Override
    public Mission update(Mission mission) {
        return missionRepository.save(mission);
    }

    @Override
    public void delete(Long id) {
        missionRepository.deleteById(id);
    }

    @Override
    public List<Mission> findByStatut(String statut) {
        return missionRepository.findByStatut(statut);
    }

    @Override
    public List<Mission> findByLocation(String prefecture, String commune) {
        return missionRepository.findByCommune(commune);
    }

    @Override
    public List<Mission> findByCreatedByUserId(String userId) {
        return missionRepository.findByCreatedByUserId(userId);
    }

    @Override
    public List<Mission> findByDateRange(Date startDate, Date endDate) {
        return missionRepository.findByDateEnvoiBetween(startDate, endDate);
    }

    @Override
    public List<Mission> findCompletedMissions() {
        return missionRepository.findCompletedMissions();
    }

    @Override
    public List<Mission> findActiveMissions() {
        return missionRepository.findActiveMissions();
    }

    @Override
    public void completeMission(Long id) {
        Optional<Mission> mission = missionRepository.findById(id);
        if (mission.isPresent()) {
            mission.get().complete();
            missionRepository.save(mission.get());
        }
    }
}