package com.example.HNR.Controller;

import com.example.HNR.Model.Douar;
import com.example.HNR.Model.StatutDouar;
import com.example.HNR.Service.DouarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/douars")
@CrossOrigin(origins = "*")
public class DouarController {

    @Autowired
    private DouarService douarService;

    // GET /api/douars - Obtenir tous les douars
    @GetMapping
    public ResponseEntity<List<Douar>> getAllDouars() {
        List<Douar> douars = douarService.findAll();
        return ResponseEntity.ok(douars);
    }

    // GET /api/douars/{id} - Obtenir douar par ID
    @GetMapping("/{id}")
    public ResponseEntity<Douar> getDouarById(@PathVariable String id) {
        Optional<Douar> douar = douarService.findById(id);
        if (douar.isPresent()) {
            return ResponseEntity.ok(douar.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/douars/nom/{nom} - Obtenir douar par nom
    @GetMapping("/nom/{nom}")
    public ResponseEntity<Douar> getDouarByNom(@PathVariable String nom) {
        Optional<Douar> douar = douarService.findByNom(nom);
        if (douar.isPresent()) {
            return ResponseEntity.ok(douar.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/douars/statut/{statut} - Obtenir douars par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Douar>> getDouarsByStatut(@PathVariable StatutDouar statut) {
        List<Douar> douars = douarService.findByStatut(statut);
        return ResponseEntity.ok(douars);
    }

    // GET /api/douars/prefecture/{prefecture} - Obtenir douars par préfecture
    @GetMapping("/prefecture/{prefecture}")
    public ResponseEntity<List<Douar>> getDouarsByPrefecture(@PathVariable String prefecture) {
        List<Douar> douars = douarService.findByPrefectureCommune(prefecture);
        return ResponseEntity.ok(douars);
    }

    // GET /api/douars/statut/{statut}/prefecture/{prefecture} - Obtenir douars par statut et préfecture
    @GetMapping("/statut/{statut}/prefecture/{prefecture}")
    public ResponseEntity<List<Douar>> getDouarsByStatutAndPrefecture(@PathVariable StatutDouar statut, @PathVariable String prefecture) {
        List<Douar> douars = douarService.findByStatutAndPrefectureCommune(statut, prefecture);
        return ResponseEntity.ok(douars);
    }

    // GET /api/douars/mission/{missionId} - Obtenir douars par mission
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<Douar>> getDouarsByMission(@PathVariable String missionId) {
        List<Douar> douars = douarService.findByMission(missionId);
        return ResponseEntity.ok(douars);
    }

    // GET /api/douars/search/{nom} - Rechercher douars par nom
    @GetMapping("/search/{nom}")
    public ResponseEntity<List<Douar>> searchDouarsByName(@PathVariable String nom) {
        List<Douar> douars = douarService.searchByName(nom);
        return ResponseEntity.ok(douars);
    }

    // GET /api/douars/with-actions - Obtenir douars avec actions
    @GetMapping("/with-actions")
    public ResponseEntity<List<Douar>> getDouarsWithActions() {
        List<Douar> douars = douarService.findDouarsWithActions();
        return ResponseEntity.ok(douars);
    }

    // POST /api/douars - Créer nouveau douar
    @PostMapping
    public ResponseEntity<Douar> createDouar(@RequestBody Douar douar) {
        try {
            Douar createdDouar = douarService.createDouar(douar);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDouar);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/douars/{id} - Mettre à jour douar
    @PutMapping("/{id}")
    public ResponseEntity<Douar> updateDouar(@PathVariable String id, @RequestBody Douar douarDetails) {
        try {
            Douar updatedDouar = douarService.updateDouar(id, douarDetails);
            return ResponseEntity.ok(updatedDouar);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/douars/{id} - Supprimer douar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDouar(@PathVariable String id) {
        try {
            douarService.deleteDouar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/douars/count/statut/{statut} - Compter par statut
    @GetMapping("/count/statut/{statut}")
    public ResponseEntity<Long> countDouarsByStatut(@PathVariable StatutDouar statut) {
        long count = douarService.countByStatut(statut);
        return ResponseEntity.ok(count);
    }

    // GET /api/douars/count/prefecture/{prefecture} - Compter par préfecture
    @GetMapping("/count/prefecture/{prefecture}")
    public ResponseEntity<Long> countDouarsByPrefecture(@PathVariable String prefecture) {
        long count = douarService.countByPrefectureCommune(prefecture);
        return ResponseEntity.ok(count);
    }
}
