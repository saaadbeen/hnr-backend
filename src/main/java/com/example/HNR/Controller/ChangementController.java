package com.example.HNR.Controller;

import com.example.HNR.DTO.ChangementDTO;
import com.example.HNR.Model.SqlServer.Changement;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.TypeExtension;
import com.example.HNR.Service.ChangementService;
import com.example.HNR.Repository.SqlServer.DouarRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/changements")
public class ChangementController {

    @Autowired private ChangementService changementService;
    @Autowired private DouarRepository douarRepository;

    private ChangementDTO toDto(Changement e) {
        ChangementDTO d = new ChangementDTO();
        d.changementId     = e.getChangementId();
        d.type             = e.getType();
        d.date             = e.getDate();
        d.detectedByUserId = e.getDetectedByUserId();
        d.createdAt        = e.getCreatedAt();
        d.updatedAt        = e.getUpdatedAt();
        d.pdfUrl           = e.getPdfUrl();

        d.titre       = e.getTitre();
        d.description = e.getDescription();
        d.prefecture  = e.getPrefecture();
        d.commune     = e.getCommune();

        d.longitude   = e.getLongitude();
        d.latitude    = e.getLatitude();
        d.pointWKT    = e.getPointWkt();
        d.polygonWKT  = e.getPolygonWkt();

        if (e.getDouar() != null) {
            d.douarId = e.getDouar().getDouarId();
        }
        return d;
    }

    private Changement fromDto(ChangementDTO d) {
        Changement e = new Changement();
        e.setChangementId(d.changementId);
        e.setType(d.type);
        e.setDate(d.date != null ? d.date : new Date()); // une seule date

        e.setDetectedByUserId(d.detectedByUserId);

        if (d.douarId != null) {
            Douar douar = douarRepository.findById(d.douarId).orElse(null);
            e.setDouar(douar);
        }

        e.setTitre(d.titre);
        e.setDescription(d.description);
        e.setPrefecture(d.prefecture);
        e.setCommune(d.commune);

        e.setLongitude(d.longitude);
        e.setLatitude(d.latitude);
        e.setPointWkt(d.pointWKT);
        e.setPolygonWkt(d.polygonWKT);

        e.setPdfUrl(d.pdfUrl);
        return e;
    }

    /* ------------------------- endpoints ------------------------- */

    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getAllChangements() {
        var dtos = changementService.findAll().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ChangementDTO> getChangementById(@PathVariable Long id) {
        return changementService.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ChangementDTO> createChangement(@Valid @RequestBody ChangementDTO dto) {
        var saved = changementService.create(fromDto(dto));
        return new ResponseEntity<>(toDto(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<ChangementDTO> updateChangement(@PathVariable Long id, @Valid @RequestBody ChangementDTO dto) {
        var existing = changementService.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        dto.changementId = id;
        var entity = fromDto(dto);
        entity.setCreatedAt(existing.get().getCreatedAt());

        var saved = changementService.update(entity);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Void> deleteChangement(@PathVariable Long id) {
        if (changementService.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        changementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getChangementsByType(@PathVariable TypeExtension type) {
        var dtos = changementService.findByType(type).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/douar/{douarId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getChangementsByDouarId(@PathVariable Long douarId) {
        var dtos = changementService.findByDouarId(douarId).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/detected-by/{userId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #userId == authentication.name")
    public ResponseEntity<List<ChangementDTO>> getChangementsByDetectedUser(@PathVariable String userId) {
        var dtos = changementService.findByDetectedByUserId(userId).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ChangementDTO>> getChangementsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        var dtos = changementService.findByDateRange(startDate, endDate).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }
}
