package com.example.HNR.Controller;

import com.example.HNR.Model.PV;

import com.example.HNR.Service.PVService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@RestController
@RequestMapping("/pvs")
public class PVController {

    @Autowired
    private PVService pvService;

    @GetMapping
    public List<PV> getAll() {
        return pvService.getAllPvs();
    }

    @GetMapping("/{id}")
    public PV getById(@PathVariable String id) {
        return pvService.getPvById(id);
    }

    @GetMapping("/mission/{missionId}")
    public List<PV> getByMission(@PathVariable String missionId) {
        return pvService.findByMissionId(missionId);
    }

    @GetMapping("/douar/{douarId}")
    public List<PV> getByDouar(@PathVariable String douarId) {
        return pvService.findByDouarId(douarId);
    }

    @GetMapping("/redacteur/{redacteurId}")
    public List<PV> getByRedacteur(@PathVariable String redacteurId) {
        return pvService.findByRedacteurId(redacteurId);
    }

    @PostMapping
    public PV create(@RequestBody PV pv) {
        return pvService.createPv(pv);
    }

    @PutMapping("/{id}")
    public PV update(@PathVariable String id, @RequestBody PV pv) {
        return pvService.updatePv(id, pv);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        pvService.deletePv(id);
    }
}