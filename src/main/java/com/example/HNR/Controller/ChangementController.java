package com.example.HNR.Controller;

import com.example.HNR.Model.Changement;
import com.example.HNR.Service.ChangementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/changements")
@CrossOrigin(origins = "*")
public class ChangementController {

    @Autowired
    private ChangementService changementService;

    // --- CRUD de base ---

    @GetMapping
    public List<Changement> getAllChangements() {
        return changementService.getAllChangements();
    }

    @GetMapping("/{id}")
    public Changement getChangementById(@PathVariable String id) {
        return changementService.getChangementById(id).orElse(null);
    }

    @PostMapping
    public Changement createChangement(@RequestBody Changement changement) {
        return changementService.saveChangement(changement);
    }

    @PutMapping("/{id}")
    public Changement updateChangement(@PathVariable String id, @RequestBody Changement changement) {
        changement.setId(id);
        return changementService.saveChangement(changement);
    }

    @DeleteMapping("/{id}")
    public void deleteChangement(@PathVariable String id) {
        changementService.deleteChangement(id);
    }

    // --- Requêtes personnalisées ---

    // 1. Lister les changements pour un douar donné
    @GetMapping("/douar/{douarId}")
    public List<Changement> getChangementsByDouar(@PathVariable String douarId) {
        return changementService.getChangementsByDouarId(douarId);
    }

    // 2. Récupérer un changement par son code
    @GetMapping("/code/{code}")
    public Changement getChangementByCode(@PathVariable String code) {
        return changementService.getChangementByCode(code);
    }
}
