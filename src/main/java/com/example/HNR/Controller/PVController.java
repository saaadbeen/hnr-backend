package com.example.HNR.Controller;

import com.example.HNR.Model.PV;
import com.example.HNR.Service.PVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pvs")
@CrossOrigin(origins = "*")
public class PVController {

    @Autowired
    private PVService pvService;

    // --- CRUD de base ---

    @GetMapping
    public List<PV> getAllPVs() {
        return pvService.getAllPVs();
    }

    @GetMapping("/{id}")
    public PV getPVById(@PathVariable String id) {
        return pvService.getPVById(id).orElse(null);
    }

    @PostMapping
    public PV createPV(@RequestBody PV pv) {
        return pvService.savePV(pv);
    }

    @PutMapping("/{id}")
    public PV updatePV(@PathVariable String id, @RequestBody PV pv) {
        pv.setId(id);
        return pvService.savePV(pv);
    }

    @DeleteMapping("/{id}")
    public void deletePV(@PathVariable String id) {
        pvService.deletePV(id);
    }

    // --- Requête personnalisée ---

    @GetMapping("/douar/{douarId}")
    public List<PV> getPVsByDouar(@PathVariable String douarId) {
        return pvService.getPVsByDouarId(douarId);
    }
}
