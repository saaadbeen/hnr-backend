package com.example.HNR.Controller;

import com.example.HNR.DTO.ActionLightDTO;
import com.example.HNR.DTO.PvDTO;
import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.SqlServer.PV;
import com.example.HNR.Repository.SqlServer.PVRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur PV : renvoie des DTO pour éviter toute fuite d’entités (lazy)
 */
@RestController
@RequestMapping("/api/pvs")
@RequiredArgsConstructor
public class PVController {

    private final PVRepository pvRepository;
    private final ObjectMapper objectMapper;
    @PutMapping("/{id}")
    public ResponseEntity<PvDTO> update(@PathVariable Long id, @RequestBody PvDTO body) {
        Optional<PV> opt = pvRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        PV pv = opt.get();
        // sérialise le contenu reçu (Map) → String
        try {
            String json = body.getContenu() != null ? objectMapper.writeValueAsString(body.getContenu()) : "{}";
            pv.setContenu(json);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        pv.setUpdatedAt(new Date());
        // (optionnel) statut/valide etc.
        pvRepository.save(pv);
        return ResponseEntity.ok(toDto(pv));
    }
    @GetMapping("/{id}")
    public ResponseEntity<PvDTO> getById(@PathVariable Long id) {
        Optional<PV> opt = pvRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(opt.get()));
    }

    @GetMapping("/by-action/{actionId}")
    public ResponseEntity<PvDTO> getByAction(@PathVariable Long actionId) {
        Optional<PV> opt = pvRepository.findByAction_ActionId(actionId);
        if (opt.isEmpty()) {
            // 404 volontaire : ton front s’attend à null lorsque pas de PV
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDto(opt.get()));
    }

    /* =======================
       Mapping Entity -> DTO
       ======================= */
    private PvDTO toDto(PV pv) {
        PvDTO dto = new PvDTO();

        Long id = safePvId(pv);
        dto.setId(id);
        dto.setPvId(id);

        // Contenu JSON -> Map
        dto.setContenu(readJson(pv.getContenu()));

        // Statut lisible pour le front
        boolean valide = safeBoolean(pv.getValide());
        dto.setStatut(valide ? "PUBLIE" : "BROUILLON");

        // Dates si présentes dans l’entité
        dto.setCreatedAt(safeDate(pv.getCreatedAt()));
        dto.setUpdatedAt(safeDate(pv.getUpdatedAt()));

        // Action light
        Action a = pv.getAction();
        if (a != null) {
            ActionLightDTO al = new ActionLightDTO();
            al.setActionId(a.getActionId());
            al.setType(a.getType() != null ? a.getType().name() : null);

            Date d = a.getDateAction();
            al.setDate(d);
            al.setDateAction(d);

            al.setPrefecture(a.getPrefecture());
            al.setCommune(a.getCommune());

            // On ne renvoie que les IDs pour éviter tout lazy
            al.setDouarId(a.getDouar() != null ? a.getDouar().getDouarId() : null);
            al.setMissionId(a.getMission() != null ? a.getMission().getMissionId() : null);

            al.setPhotoAvantUrl(a.getPhotoAvantUrl());
            al.setPhotoApresUrl(a.getPhotoApresUrl());
            al.setUserId(a.getUserId());

            dto.setAction(al);
            dto.setActionId(a.getActionId());
            // Type de PV = type de l’action (utile pour PVEditor)
            dto.setType(al.getType());
        }

        return dto;
    }

    /* =======================
       Helpers sûrs
       ======================= */
    private Map<String, Object> readJson(String json) {
        try {
            if (json == null || json.isBlank()) return null;
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return null; // on évite une 500 si JSON invalide
        }
    }

    private Long safePvId(PV pv) {
        try { return (Long) PV.class.getMethod("getPvId").invoke(pv); }
        catch (Exception ignore) {}
        try { return (Long) PV.class.getMethod("getId").invoke(pv); }
        catch (Exception ignore) {}
        return null;
    }

    private boolean safeBoolean(Boolean b) {
        return b != null && b;
    }

    private Date safeDate(Date d) { return d; }
}
