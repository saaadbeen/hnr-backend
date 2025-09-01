package com.example.HNR.Controller;

import com.example.HNR.DTO.DouarDTO;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.SqlServer.Mission;
import com.example.HNR.Model.enums.StatutDouar;
import com.example.HNR.Repository.SqlServer.MissionRepository;
import com.example.HNR.Service.DouarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/douars")
public class DouarController {

    @Autowired
    private DouarService douarService;

    @Autowired
    private MissionRepository missionRepository;

    // GET all douars - DTO
    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<DouarDTO>> getAllDouars() {
        var douars = douarService.findAll(Pageable.unpaged()).getContent();
        var dtos = douars.stream().map(this::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // GET douar by id - DTO
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<DouarDTO> getDouarById(@PathVariable Long id) {
        Optional<Douar> douar = douarService.findById(id);
        return douar.map(value -> new ResponseEntity<>(toDto(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST create douar - DTO
    @PostMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<DouarDTO> createDouar(@RequestBody DouarDTO request) {
        if (request.getNom() == null || request.getNom().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (request.getPrefecture() == null || request.getCommune() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Douar> existingDouar = douarService.findByNom(request.getNom());
        if (existingDouar.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Douar toSave = toEntity(request);
        Douar saved = douarService.create(toSave);
        return new ResponseEntity<>(toDto(saved), HttpStatus.CREATED);
    }

    // PUT update douar - DTO
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@douarServiceImpl.findById(#id).isPresent() and @douarServiceImpl.findById(#id).get().getCreatedByUserId() == authentication.name)")
    public ResponseEntity<DouarDTO> updateDouar(@PathVariable Long id, @RequestBody DouarDTO request) {
        Optional<Douar> existing = douarService.findById(id);
        if (existing.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Douar current = existing.get();
        if (request.getNom() != null && !request.getNom().equals(current.getNom())) {
            Optional<Douar> dup = douarService.findByNom(request.getNom());
            if (dup.isPresent() && !dup.get().getDouarId().equals(id)) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }

        // Map request onto entity while preserving immutable metadata
        current.setNom(request.getNom());
        current.setStatut(request.getStatut());
        current.setPrefecture(request.getPrefecture());
        current.setCommune(request.getCommune());
        current.setLatitude(request.getLatitude());
        current.setLongitude(request.getLongitude());
        current.setCreatedByUserId(request.getCreatedByUserId());
        if (request.getMissionId() != null) {
            Mission mission = missionRepository.findById(request.getMissionId())
                    .orElseThrow(() -> new IllegalArgumentException("Mission introuvable: " + request.getMissionId()));
            current.setMission(mission);
        } else {
            current.setMission(null);
        }

        Douar updated = douarService.update(current);
        return new ResponseEntity<>(toDto(updated), HttpStatus.OK);
    }

    // DELETE douar (soft delete)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Void> deleteDouar(@PathVariable Long id) {
        Optional<Douar> douar = douarService.findById(id);
        if (douar.isPresent()) {
            douarService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET douar by nom - DTO
    @GetMapping("/nom/{nom}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<DouarDTO> getDouarByNom(@PathVariable String nom) {
        Optional<Douar> douar = douarService.findByNom(nom);
        return douar.map(value -> new ResponseEntity<>(toDto(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET douars by mission ID - DTO
    @GetMapping("/mission/{missionId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<DouarDTO>> getDouarsByMissionId(@PathVariable Long missionId) {
        List<DouarDTO> dtos = douarService.findByMissionId(missionId).stream()
                .map(this::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // GET douars by creator user ID - DTO
    @GetMapping("/created-by/{userId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #userId == authentication.name")
    public ResponseEntity<List<DouarDTO>> getDouarsByCreator(@PathVariable String userId) {
        List<DouarDTO> dtos = douarService.findByCreatedByUserId(userId).stream()
                .map(this::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // GET active douars - DTO
    @GetMapping("/active")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<DouarDTO>> getActiveDouars() {
        List<DouarDTO> dtos = douarService.findActiveDouars().stream()
                .map(this::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // GET douars by current user - DTO
    @GetMapping("/my-douars")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<DouarDTO>> getMyDouars() {
        String currentUserId = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        List<DouarDTO> dtos = douarService.findByCreatedByUserId(currentUserId).stream()
                .map(this::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // GET douars éradiqués - DTO
    @GetMapping("/eradiques")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<DouarDTO>> getDouarsEradiques() {
        List<DouarDTO> dtos = douarService.findByStatut(StatutDouar.ERADIQUE).stream()
                .map(this::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // GET douars non éradiqués - DTO
    @GetMapping("/non-eradiques")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<DouarDTO>> getDouarsNonEradiques() {
        List<DouarDTO> dtos = douarService.findByStatut(StatutDouar.NON_ERADIQUE).stream()
                .map(this::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // PUT éradiquer douar - DTO
    @PutMapping("/{id}/eradiquer")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<DouarDTO> eradiquerDouar(@PathVariable Long id) {
        Optional<Douar> existingDouar = douarService.findById(id);
        if (existingDouar.isPresent()) {
            douarService.eradiquerDouar(id);
            Optional<Douar> updatedDouar = douarService.findById(id);
            return updatedDouar.map(d -> new ResponseEntity<>(toDto(d), HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT update coordinates - DTO
    @PutMapping("/{id}/coordinates")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@douarServiceImpl.findById(#id).isPresent() and @douarServiceImpl.findById(#id).get().getCreatedByUserId() == authentication.name)")
    public ResponseEntity<DouarDTO> updateDouarCoordinates(
            @PathVariable Long id,
            @RequestBody CoordinatesUpdateRequest request) {
        Optional<Douar> existingDouar = douarService.findById(id);
        if (existingDouar.isPresent()) {
            if (request.getLatitude() == null || request.getLongitude() == null ||
                    request.getLatitude() < -90 || request.getLatitude() > 90 ||
                    request.getLongitude() < -180 || request.getLongitude() > 180) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Douar douar = existingDouar.get();
            douar.setLatitude(request.getLatitude());
            douar.setLongitude(request.getLongitude());

            Douar updatedDouar = douarService.update(douar);
            return new ResponseEntity<>(toDto(updatedDouar), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Mapping helpers
    private DouarDTO toDto(Douar e) {
        DouarDTO d = new DouarDTO();
        d.setId(e.getDouarId());
        d.setNom(e.getNom());
        d.setStatut(e.getStatut());
        d.setPrefecture(e.getPrefecture());
        d.setCommune(e.getCommune());
        d.setLatitude(e.getLatitude());
        d.setLongitude(e.getLongitude());
        d.setMissionId(e.getMission() != null ? e.getMission().getMissionId() : null);
        d.setCreatedByUserId(e.getCreatedByUserId());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    private Douar toEntity(DouarDTO d) {
        Douar e = new Douar();
        e.setDouarId(d.getId());
        e.setNom(d.getNom());
        e.setStatut(d.getStatut());
        e.setPrefecture(d.getPrefecture());
        e.setCommune(d.getCommune());
        e.setLatitude(d.getLatitude());
        e.setLongitude(d.getLongitude());
        e.setCreatedByUserId(d.getCreatedByUserId());
        if (d.getMissionId() != null) {
            Mission mission = missionRepository.findById(d.getMissionId())
                    .orElseThrow(() -> new IllegalArgumentException("Mission introuvable: " + d.getMissionId()));
            e.setMission(mission);
        }
        return e;
    }

    // Payload for coordinates update
    public static class CoordinatesUpdateRequest {
        private Double latitude;
        private Double longitude;

        public CoordinatesUpdateRequest() {}
        public CoordinatesUpdateRequest(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
}

