package com.example.HNR.Controller;

import com.example.HNR.Model.Changement;


import com.example.HNR.Model.Mission;
import com.example.HNR.Service.ChangementService;
import com.example.HNR.Service.MissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @GetMapping
    public List<Mission> getAll() {
        return missionService.getAllMissions();
    }

    @GetMapping("/{id}")
    public Optional<Mission> getById(@PathVariable String id) {
        return missionService.getMissionById(id);
    }

    @PostMapping
    public Mission create(@RequestBody Mission mission) {
        return missionService.createMission(mission);
    }

    @PutMapping("/{id}")
    public Mission update(@PathVariable String id, @RequestBody Mission mission) {
        return missionService.updateMission(id, mission);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        missionService.deleteMission(id);
    }

    @GetMapping("/prefecture/{prefecture}")
    public List<Mission> getByPrefecture(@PathVariable String prefecture) {
        return missionService.getByPrefecture(prefecture);
    }

    @GetMapping("/commune/{commune}")
    public List<Mission> getByCommune(@PathVariable String commune) {
        return missionService.getByCommune(commune);
    }

    @GetMapping("/createur/{userId}")
    public List<Mission> getByCreateur(@PathVariable String userId) {
        return missionService.getByCreateurId(userId);
    }

    @GetMapping("/search")
    public List<Mission> searchByNom(@RequestParam String nom) {
        return missionService.searchByNom(nom);
    }
}