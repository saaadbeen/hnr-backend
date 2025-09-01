package com.example.HNR.Controller;

import com.example.HNR.DTO.ChangementDTO;
import com.example.HNR.Model.SqlServer.Changement;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.TypeExtension;
import com.example.HNR.Service.ChangementService;
import com.example.HNR.Repository.SqlServer.DouarRepository;
// import com.example.HNR.Service.FichierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/changements")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
public class ChangementController {

    @Autowired private ChangementService changementService;
    @Autowired private DouarRepository douarRepository;
    @Autowired private FichierService fichierService; // pour recuperer les PDF associes

    // ---------- mapping manuel ----------
    private ChangementDTO toDto(Changement e) {
        ChangementDTO d = new ChangementDTO();
        d.changementId    = e.getChangementId();
        d.type            = e.getType();
        d.dateBefore      = e.getDateBefore();
        d.dateAfter       = e.getDateAfter();
        d.surface         = e.getSurface();
        d.douarId         = (e.getDouar() != null ? e.getDouar().getDouarId() : null);
        d.detectedByUserId= e.getDetectedByUserId();
        d.createdAt       = e.getCreatedAt();
        d.updatedAt       = e.getUpdatedAt();

        // // Optionnel: dernier PDF attaché
        // var pdfs = fichierService.findByEntity("CHANGEMENT_PDF", e.getChangementId());
        // d.pdfUrl = pdfs.isEmpty() ? null : pdfs.get(0).getUrl();
        return d;
    }

    private Changement fromDto(ChangementDTO d) {
        Changement e = new Changement();
        e.setChangementId(d.changementId);
        e.setType(d.type);
        e.setDateBefore(d.dateBefore);
        e.setDateAfter(d.dateAfter);
        e.setSurface(d.surface);
        e.setDetectedByUserId(d.detectedByUserId);

        if (d.douarId != null) {
            Douar douar = douarRepository.findById(d.douarId)
                    .orElseThrow(() -> new IllegalArgumentException("Douar introuvable: " + d.douarId));
            e.setDouar(douar);
        } else {
            e.setDouar(null);
        }
        return e;
    }
    // -----------------------------------

    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getAllChangements() {
        try {
            var dtos = changementService.findAll().stream().map(this::toDto).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ChangementDTO> getChangementById(@PathVariable Long id) {
        try {
            return changementService.findById(id)
                    .map(this::toDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ChangementDTO> createChangement(@RequestBody ChangementDTO dto) {
        try {
            if (dto.dateBefore == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            var saved = changementService.create(fromDto(dto));
            return new ResponseEntity<>(toDto(saved), HttpStatus.CREATED);
        } catch (IllegalArgumentException notFound) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@changementServiceImpl.findById(#id).isPresent() and @changementServiceImpl.findById(#id).get().getDetectedByUserId() == authentication.name)")
    public ResponseEntity<ChangementDTO> updateChangement(@PathVariable Long id, @RequestBody ChangementDTO dto) {
        try {
            var existing = changementService.findById(id);
            if (existing.isEmpty()) return ResponseEntity.notFound().build();

            dto.changementId = id;
            var entity = fromDto(dto);
            // préserver createdAt si géré par l’entité
            entity.setCreatedAt(existing.get().getCreatedAt());

            var saved = changementService.update(entity);
            return ResponseEntity.ok(toDto(saved));
        } catch (IllegalArgumentException notFound) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Void> deleteChangement(@PathVariable Long id) {
        try {
            if (changementService.findById(id).isEmpty()) return ResponseEntity.notFound().build();
            changementService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Filtres — mêmes signatures, mais retours en DTO
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getChangementsByType(@PathVariable TypeExtension type) {
        try {
            var dtos = changementService.findByType(type).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/douar/{douarId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getChangementsByDouarId(@PathVariable Long douarId) {
        try {
            var dtos = changementService.findByDouarId(douarId).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/detected-by/{userId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #userId == authentication.name")
    public ResponseEntity<List<ChangementDTO>> getChangementsByDetectedUser(@PathVariable String userId) {
        try {
            var dtos = changementService.findByDetectedByUserId(userId).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getChangementsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            var dtos = changementService.findByDateRange(startDate, endDate).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/surface-minimum")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getChangementsBySurfaceMinimum(@RequestParam Double minSurface) {
        try {
            if (minSurface < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            var dtos = changementService.findBySurfaceMinimum(minSurface).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/my-changements")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getMyChangements() {
        try {
            String currentUserId = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();
            var dtos = changementService.findByDetectedByUserId(currentUserId).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/horizontal")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getHorizontalExtensions() {
        try {
            var dtos = changementService.findByType(TypeExtension.HORIZONTAL).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/vertical")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getVerticalExtensions() {
        try {
            var dtos = changementService.findByType(TypeExtension.VERTICAL).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

}
