package com.example.HNR.Controller;

import com.example.HNR.Model.Changement;
import com.example.HNR.Model.TypeExtension;
import com.example.HNR.Service.ChangementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/changements")
@CrossOrigin(origins = "*")
public class ChangementController {

    @Autowired
    private ChangementService changementService;

    // GET /api/changements - Obtenir tous les changements
    @GetMapping
    public ResponseEntity<List<Changement>> getAllChangements() {
        List<Changement> changements = changementService.findAll();
        return ResponseEntity.ok(changements);
    }

    // GET /api/changements/{id} - Obtenir changement par ID
    @GetMapping("/{id}")
    public ResponseEntity<Changement> getChangementById(@PathVariable String id) {
        Optional<Changement> changement = changementService.findById(id);
        if (changement.isPresent()) {
            return ResponseEntity.ok(changement.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/changements/type/{type} - Obtenir changements par type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Changement>> getChangementsByType(@PathVariable TypeExtension type) {
        List<Changement> changements = changementService.findByType(type);
        return ResponseEntity.ok(changements);
    }

    // GET /api/changements/douar/{douarId} - Obtenir changements par douar
    @GetMapping("/douar/{douarId}")
    public ResponseEntity<List<Changement>> getChangementsByDouar(@PathVariable String douarId) {
        List<Changement> changements = changementService.findByDouar(douarId);
        return ResponseEntity.ok(changements);
    }

    // GET /api/changements/surface/{minSurface} - Changements par surface minimale
    @GetMapping("/surface/{minSurface}")
    public ResponseEntity<List<Changement>> getChangementsBySurface(@PathVariable double minSurface) {
        List<Changement> changements = changementService.findBySurfaceGreaterThanEqual(minSurface);
        return ResponseEntity.ok(changements);
    }

    // GET /api/changements/with-photos - Changements avec photos avant et après
    @GetMapping("/with-photos")
    public ResponseEntity<List<Changement>> getChangementsWithPhotos() {
        List<Changement> changements = changementService.findChangementsWithBothPhotos();
        return ResponseEntity.ok(changements);
    }

    // GET /api/changements/douar/{douarId}/surface-totale - Surface totale par douar
    @GetMapping("/douar/{douarId}/surface-totale")
    public ResponseEntity<Double> getTotalSurfaceByDouar(@PathVariable String douarId) {
        double totalSurface = changementService.getTotalSurfaceByDouar(douarId);
        return ResponseEntity.ok(totalSurface);
    }

    // POST /api/changements - Créer nouveau changement
    @PostMapping
    public ResponseEntity<Changement> createChangement(@RequestBody Changement changement) {
        Changement createdChangement = changementService.createChangement(changement);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChangement);
    }

    // PUT /api/changements/{id} - Mettre à jour changement
    @PutMapping("/{id}")
    public ResponseEntity<Changement> updateChangement(@PathVariable String id, @RequestBody Changement changementDetails) {
        try {
            Changement updatedChangement = changementService.updateChangement(id, changementDetails);
            return ResponseEntity.ok(updatedChangement);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/changements/{id} - Supprimer changement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChangement(@PathVariable String id) {
        try {
            changementService.deleteChangement(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/changements/count/type/{type} - Compter par type
    @GetMapping("/count/type/{type}")
    public ResponseEntity<Long> countChangementsByType(@PathVariable TypeExtension type) {
        long count = changementService.countByType(type);
        return ResponseEntity.ok(count);
    }
}
