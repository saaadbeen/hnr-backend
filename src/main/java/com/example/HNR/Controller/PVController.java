package com.example.HNR.Controller;

import com.example.HNR.Model.SqlServer.PV;
import com.example.HNR.Service.PVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pvs")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
public class PVController {

    @Autowired
    private PVService pvService;

    // GET all PVs - accessible à tous les rôles authentifiés
    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<PV>> getAllPVs() {
        try {
            List<PV> pvs = pvService.findAll();
            return new ResponseEntity<>(pvs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET PV by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<PV> getPVById(@PathVariable Long id) {
        try {
            Optional<PV> pv = pvService.findById(id);
            if (pv.isPresent()) {
                return new ResponseEntity<>(pv.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST create PV - accessible à tous les rôles authentifiés
    @PostMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<PV> createPV(@RequestBody PV pv) {
        try {
            // Validation basique
            if (pv.getContenu() == null || pv.getContenu().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (pv.getRedacteurUserId() == null || pv.getRedacteurUserId().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Définir la date de rédaction si non fournie
            if (pv.getDateRedaction() == null) {
                pv.setDateRedaction(new Date());
            }

            PV savedPV = pvService.create(pv);
            return new ResponseEntity<>(savedPV, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update PV - rédacteur + GOUVERNEUR/MEMBRE_DSI
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@pvServiceImpl.findById(#id).isPresent() and @pvServiceImpl.findById(#id).get().getRedacteurUserId() == authentication.name)")
    public ResponseEntity<PV> updatePV(@PathVariable Long id, @RequestBody PV pv) {
        try {
            Optional<PV> existingPV = pvService.findById(id);
            if (existingPV.isPresent()) {
                pv.setPvId(id);
                // Conserver les métadonnées originales
                pv.setCreatedAt(existingPV.get().getCreatedAt());

                PV updatedPV = pvService.update(pv);
                return new ResponseEntity<>(updatedPV, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE PV - accessible aux GOUVERNEUR et MEMBRE_DSI
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Void> deletePV(@PathVariable Long id) {
        try {
            Optional<PV> pv = pvService.findById(id);
            if (pv.isPresent()) {
                pvService.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET PVs by rédacteur user ID
    @GetMapping("/redacteur/{userId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #userId == authentication.name")
    public ResponseEntity<List<PV>> getPVsByRedacteur(@PathVariable String userId) {
        try {
            List<PV> pvs = pvService.findByRedacteurUserId(userId);
            return new ResponseEntity<>(pvs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET PV by action ID
    @GetMapping("/action/{actionId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<PV> getPVByActionId(@PathVariable Long actionId) {
        try {
            Optional<PV> pv = pvService.findByActionId(actionId);
            if (pv.isPresent()) {
                return new ResponseEntity<>(pv.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET PVs by date range
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<PV>> getPVsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            List<PV> pvs = pvService.findByDateRange(startDate, endDate);
            return new ResponseEntity<>(pvs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET PVs with PDF
    @GetMapping("/with-pdf")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<PV>> getPVsWithPDF() {
        try {
            List<PV> pvs = pvService.findPVsWithPDF();
            return new ResponseEntity<>(pvs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET PVs by current user (rédigés par l'utilisateur connecté)
    @GetMapping("/my-pvs")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<PV>> getMyPVs() {
        try {
            String currentUserId = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();

            List<PV> pvs = pvService.findByRedacteurUserId(currentUserId);
            return new ResponseEntity<>(pvs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update PDF URL
    @PutMapping("/{id}/pdf")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@pvServiceImpl.findById(#id).isPresent() and @pvServiceImpl.findById(#id).get().getRedacteurUserId() == authentication.name)")
    public ResponseEntity<PV> updatePVPDF(
            @PathVariable Long id,
            @RequestBody PDFUpdateRequest request) {
        try {
            Optional<PV> existingPV = pvService.findById(id);
            if (existingPV.isPresent()) {
                PV pv = existingPV.get();
                pv.setUrlPDF(request.getUrlPDF());

                PV updatedPV = pvService.update(pv);
                return new ResponseEntity<>(updatedPV, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET recent PVs (ordre décroissant par date de rédaction)
    @GetMapping("/recent")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<PV>> getRecentPVs() {
        try {
            List<PV> pvs = pvService.findAll(); // Le service peut implémenter un ordre par défaut
            // Trier par date de rédaction décroissante
            pvs.sort((pv1, pv2) -> pv2.getDateRedaction().compareTo(pv1.getDateRedaction()));

            // Limiter aux 20 plus récents
            List<PV> recentPVs = pvs.stream().limit(20).toList();

            return new ResponseEntity<>(recentPVs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Classe interne pour la mise à jour du PDF
    public static class PDFUpdateRequest {
        private String urlPDF;

        public PDFUpdateRequest() {}
        public PDFUpdateRequest(String urlPDF) {
            this.urlPDF = urlPDF;
        }

        public String getUrlPDF() { return urlPDF; }
        public void setUrlPDF(String urlPDF) { this.urlPDF = urlPDF; }
    }
}