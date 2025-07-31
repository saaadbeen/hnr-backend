package com.example.HNR.Controller;

import com.example.HNR.Model.Douar;
import com.example.HNR.Service.DouarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/douars")
public class DouarController {

    @Autowired
    private DouarService douarService;

    @GetMapping
    public List<Douar> getAll() {
        return douarService.getAllDouars();
    }

    @GetMapping("/{id}")
    public Douar getById(@PathVariable String id) {
        return douarService.getDouarById(id);
    }

    @GetMapping("/mission/{missionId}")
    public List<Douar> getByMission(@PathVariable String missionId) {
        return douarService.findByMissionId(missionId);
    }

    @PostMapping
    public Douar create(@RequestBody Douar douar) {
        return douarService.createDouar(douar);
    }

    @PutMapping("/{id}")
    public Douar update(@PathVariable String id, @RequestBody Douar douar) {
        return douarService.updateDouar(id, douar);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        douarService.deleteDouar(id);
    }
}