package com.example.HNR.Controller;

import com.example.HNR.Model.PV;
import com.example.HNR.Service.PVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pvs")
@CrossOrigin(origins = "*")
public class PVController {

    @Autowired
    private PVService pvService;

    // GET /api/pvs - Obtenir tous les PVs
    @GetMapping
    public ResponseEntity<List<PV>> getAllPVs() {
        List<PV> pvs = pvService.findAll();
        return ResponseEntity.ok(pvs);
    }

    // GET /api/pvs/{id} - Obtenir PV par ID
    @GetMapping("/{id}")
    public ResponseEntity<PV> getPVById(@PathVariable String id) {
        Optional<PV> pv = pvService.findById(id);
        if (pv.isPresent()) {
            return ResponseEntity.ok(pv.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/pvs/numero/{numero} - Obtenir PV par numéro
    @GetMapping("/numero/{numero}")
    public ResponseEntity<PV> getPVByNumero(@PathVariable String numero) {
        Optional<PV> pv = pvService.findByNumero(numero);
        if (pv.isPresent()) {
            return ResponseEntity.ok(pv.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/pvs/redacteur/{redacteur} - Obtenir PVs par rédacteur
    @GetMapping("/redacteur/{redacteur}")
    public ResponseEntity<List<PV>> getPVsByRedacteur(@PathVariable String redacteur) {
        List<PV> pvs = pvService.findByRedacteur(redacteur);
        return ResponseEntity.ok(pvs);
    }

    // GET /api/pvs/valide/{valide} - Obtenir PVs par statut de validation
    @GetMapping("/valide/{valide}")
    public ResponseEntity<List<PV>> getPVsByStatut(@PathVariable boolean valide) {
        List<PV> pvs = pvService.findByValide(valide);
        return ResponseEntity.ok(pvs);
    }

    // GET /api/pvs/periode - PVs entre deux dates
    @GetMapping("/periode")
    public ResponseEntity<List<PV>> getPVsByPeriode(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<PV> pvs = pvService.findByDateRedactionBetween(startDate, endDate);
        return ResponseEntity.ok(pvs);
    }

    // GET /api/pvs/valides - PVs validés
    @GetMapping("/valides")
    public ResponseEntity<List<PV>> getValidatedPVs() {
        List<PV> pvs = pvService.findValidatedPVs();
        return ResponseEntity.ok(pvs);
    }

    // GET /api/pvs/en-attente - PVs en attente de validation
    @GetMapping("/en-attente")
    public ResponseEntity<List<PV>> getPendingPVs() {
        List<PV> pvs = pvService.findPendingPVs();
        return ResponseEntity.ok(pvs);
    }

    // GET /api/pvs/redacteur/{redacteur}/recents - PVs récents par rédacteur
    @GetMapping("/redacteur/{redacteur}/recents")
    public ResponseEntity<List<PV>> getRecentPVsByRedacteur(@PathVariable String redacteur) {
        List<PV> pvs = pvService.findRecentPVsByRedacteur(redacteur);
        return ResponseEntity.ok(pvs);
    }

    // POST /api/pvs - Créer nouveau PV
    @PostMapping
    public ResponseEntity<PV> createPV(@RequestBody PV pv) {
        try {
            PV createdPV = pvService.createPV(pv);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPV);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/pvs/{id} - Mettre à jour PV
    @PutMapping("/{id}")
    public ResponseEntity<PV> updatePV(@PathVariable String id, @RequestBody PV pvDetails) {
        try {
            PV updatedPV = pvService.updatePV(id, pvDetails);
            return ResponseEntity.ok(updatedPV);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/pvs/{id}/valider - Valider un PV (sans signature)
    @PutMapping("/{id}/valider")
    public ResponseEntity<PV> validatePV(@PathVariable String id) {
        try {
            PV validatedPV = pvService.validatePV(id);
            return ResponseEntity.ok(validatedPV);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/pvs/{id} - Supprimer PV
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePV(@PathVariable String id) {
        try {
            pvService.deletePV(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/pvs/count/valide/{valide} - Compter PVs par statut de validation
    @GetMapping("/count/valide/{valide}")
    public ResponseEntity<Long> countPVsByValide(@PathVariable boolean valide) {
        long count = pvService.countByValide(valide);
        return ResponseEntity.ok(count);
    }
}