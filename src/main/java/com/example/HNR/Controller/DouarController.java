package com.example.HNR.Controller;

import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.StatutDouar;
import com.example.HNR.Service.DouarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/douars")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
public class DouarController {

    @Autowired
    private DouarService douarService;

    // GET all douars - accessible à tous les rôles authentifiés
    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Douar>> getAllDouars() {
        try {
            List<Douar> douars = douarService.findAll();
            return new ResponseEntity<>(douars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET douar by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Douar> getDouarById(@PathVariable Long id) {
        try {
            Optional<Douar> douar = douarService.findById(id);
            if (douar.isPresent()) {
                return new ResponseEntity<>(douar.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST create douar - accessible à tous les rôles authentifiés
    @PostMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Douar> createDouar(@RequestBody Douar douar) {
        try {
            // Validation basique
            if (douar.getNom() == null || douar.getNom().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (douar.getPrefecture() == null || douar.getCommune() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Vérifier si un douar avec ce nom existe déjà
            Optional<Douar> existingDouar = douarService.findByNom(douar.getNom());
            if (existingDouar.isPresent()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
            }

            Douar savedDouar = douarService.create(douar);
            return new ResponseEntity<>(savedDouar, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update douar - créateur + GOUVERNEUR/MEMBRE_DSI
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@douarServiceImpl.findById(#id).isPresent() and @douarServiceImpl.findById(#id).get().getCreatedByUserId() == authentication.name)")
    public ResponseEntity<Douar> updateDouar(@PathVariable Long id, @RequestBody Douar douar) {
        try {
            Optional<Douar> existingDouar = douarService.findById(id);
            if (existingDouar.isPresent()) {
                // Vérifier si le nouveau nom existe déjà (sauf pour le douar actuel)
                if (!existingDouar.get().getNom().equals(douar.getNom())) {
                    Optional<Douar> douarWithSameName = douarService.findByNom(douar.getNom());
                    if (douarWithSameName.isPresent()) {
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                }

                douar.setDouarId(id);
                // Conserver les métadonnées originales
                douar.setCreatedAt(existingDouar.get().getCreatedAt());
                douar.setDeletedAt(existingDouar.get().getDeletedAt());

                Douar updatedDouar = douarService.update(douar);
                return new ResponseEntity<>(updatedDouar, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE douar (soft delete) - accessible aux GOUVERNEUR et MEMBRE_DSI
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Void> deleteDouar(@PathVariable Long id) {
        try {
            Optional<Douar> douar = douarService.findById(id);
            if (douar.isPresent()) {
                douarService.delete(id); // Effectue un soft delete
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET douar by nom
    @GetMapping("/nom/{nom}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Douar> getDouarByNom(@PathVariable String nom) {
        try {
            Optional<Douar> douar = douarService.findByNom(nom);
            if (douar.isPresent()) {
                return new ResponseEntity<>(douar.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET douars by statut
    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Douar>> getDouarsByStatut(@PathVariable StatutDouar statut) {
        try {
            List<Douar> douars = douarService.findByStatut(statut);
            return new ResponseEntity<>(douars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET douars by location
    @GetMapping("/location")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Douar>> getDouarsByLocation(
            @RequestParam String prefecture,
            @RequestParam String commune) {
        try {
            List<Douar> douars = douarService.findByLocation(prefecture, commune);
            return new ResponseEntity<>(douars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET douars by mission ID
    @GetMapping("/mission/{missionId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Douar>> getDouarsByMissionId(@PathVariable Long missionId) {
        try {
            List<Douar> douars = douarService.findByMissionId(missionId);
            return new ResponseEntity<>(douars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET douars by creator user ID
    @GetMapping("/created-by/{userId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #userId == authentication.name")
    public ResponseEntity<List<Douar>> getDouarsByCreator(@PathVariable String userId) {
        try {
            List<Douar> douars = douarService.findByCreatedByUserId(userId);
            return new ResponseEntity<>(douars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET active douars (non supprimés)
    @GetMapping("/active")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Douar>> getActiveDouars() {
        try {
            List<Douar> douars = douarService.findActiveDouars();
            return new ResponseEntity<>(douars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET douars by current user (créés par l'utilisateur connecté)
    @GetMapping("/my-douars")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Douar>> getMyDouars() {
        try {
            String currentUserId = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();

            List<Douar> douars = douarService.findByCreatedByUserId(currentUserId);
            return new ResponseEntity<>(douars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET douars éradiqués
    @GetMapping("/eradiques")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Douar>> getDouarsEradiques() {
        try {
            List<Douar> douars = douarService.findByStatut(StatutDouar.ERADIQUE);
            return new ResponseEntity<>(douars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET douars non éradiqués
    @GetMapping("/non-eradiques")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Douar>> getDouarsNonEradiques() {
        try {
            List<Douar> douars = douarService.findByStatut(StatutDouar.NON_ERADIQUE);
            return new ResponseEntity<>(douars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT eradiquer douar - accessible aux GOUVERNEUR et MEMBRE_DSI
    @PutMapping("/{id}/eradiquer")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Douar> eradiquerDouar(@PathVariable Long id) {
        try {
            Optional<Douar> existingDouar = douarService.findById(id);
            if (existingDouar.isPresent()) {
                douarService.eradiquerDouar(id);
                // Récupérer le douar mis à jour
                Optional<Douar> updatedDouar = douarService.findById(id);
                return new ResponseEntity<>(updatedDouar.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update coordinates
    @PutMapping("/{id}/coordinates")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@douarServiceImpl.findById(#id).isPresent() and @douarServiceImpl.findById(#id).get().getCreatedByUserId() == authentication.name)")
    public ResponseEntity<Douar> updateDouarCoordinates(
            @PathVariable Long id,
            @RequestBody CoordinatesUpdateRequest request) {
        try {
            Optional<Douar> existingDouar = douarService.findById(id);
            if (existingDouar.isPresent()) {
                // Validation des coordonnées
                if (request.getLatitude() < -90 || request.getLatitude() > 90 ||
                        request.getLongitude() < -180 || request.getLongitude() > 180) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                Douar douar = existingDouar.get();
                douar.setLatitude(request.getLatitude());
                douar.setLongitude(request.getLongitude());

                Douar updatedDouar = douarService.update(douar);
                return new ResponseEntity<>(updatedDouar, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Classe interne pour la mise à jour des coordonnées
    public static class CoordinatesUpdateRequest {
        private Double latitude;
        private Double longitude;

        // Constructeurs
        public CoordinatesUpdateRequest() {}

        public CoordinatesUpdateRequest(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        // Getters et Setters
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
}