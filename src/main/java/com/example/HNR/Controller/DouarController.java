package com.example.HNR.Controller;

import com.example.HNR.DTO.Request.DouarCreateRequest;
import com.example.HNR.DTO.DouarDTO;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Repository.SqlServer.DouarRepository;
import com.example.HNR.Service.DouarService;
import com.example.HNR.Util.GeometryUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Polygon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/douars")
@RequiredArgsConstructor
public class DouarController {

    private final DouarService douarService;
    private final GeometryUtils geometryUtils;
    private final DouarRepository douarRepository;

    /* ------------ Mapping helpers ------------ */

    private DouarDTO toDto(Douar e) {
        DouarDTO d = new DouarDTO();
        d.setId(e.getDouarId());
        d.setNom(e.getNom());
        d.setStatut(e.getStatut());
        d.setPrefecture(e.getPrefecture());
        d.setCommune(e.getCommune());
        d.setMissionId(e.getMission() != null ? e.getMission().getMissionId() : null);
        d.setCreatedByUserId(e.getCreatedByUserId());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());

        Polygon poly = e.getGeometry();
        if (poly != null) {
            d.setGeometryType("Polygon");
            d.setGeometryWKT(poly.toText());
            d.setCoordinates(geometryUtils.polygonToGeoJsonCoordinates(poly)); // anneau extérieur [[lng,lat]...]
        }
        return d;
    }

    private void fillGeometryFromDto(DouarDTO d, Douar e) {
        // priorité au WKT si fourni, sinon coords
        if (d.getGeometryWKT() != null && !d.getGeometryWKT().isBlank()) {
            e.setGeometry(geometryUtils.wktToPolygon(d.getGeometryWKT()));
        } else if (d.getCoordinates() != null && !d.getCoordinates().isEmpty()) {
            e.setGeometry(geometryUtils.coordsToPolygon(d.getCoordinates()));
        }
    }

    private Douar fromDto(DouarDTO d) {
        Douar e = Douar.builder()
                .douarId(d.getId())
                .nom(d.getNom())
                .statut(d.getStatut())
                .prefecture(d.getPrefecture())
                .commune(d.getCommune())
                .createdByUserId(d.getCreatedByUserId())
                .build();
        fillGeometryFromDto(d, e);
        // Si besoin: lier une mission ici via un MissionRepository
        return e;
    }

    /* ---------------- Endpoints ---------------- */

    /** Liste (filtrable par préfecture/commune) */
    @GetMapping
    public ResponseEntity<List<DouarDTO>> list(
            @RequestParam(required = false) String prefecture,
            @RequestParam(required = false) String commune
    ) {
        List<Douar> list = douarService.findByLocation(prefecture, commune);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }
    @GetMapping("/location")
    public ResponseEntity<List<Douar>> byLocation(
            @RequestParam String prefecture,
            @RequestParam String commune
    ) {
        // Trim pour éviter les espaces / apostrophes qui trainent
        var list = douarRepository.findByPrefectureAndCommune(prefecture.trim(), commune.trim());
        return ResponseEntity.ok(list);
    }
    /** Détail */
    @GetMapping("/{id}")
    public ResponseEntity<DouarDTO> get(@PathVariable Long id) {
        return douarService.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Création depuis GeoJSON (page de dessin Leaflet) */
    @PostMapping
    public ResponseEntity<DouarDTO> create(@Valid @RequestBody DouarCreateRequest req) {
        Douar saved = douarService.createFromGeoJson(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    /** Création via DTO (WKTx ou coordinates) — route distincte pour éviter tout conflit */
    @PostMapping("/dto")
    public ResponseEntity<?> createFromDto(@RequestBody DouarDTO dto) {
        Map<String, String> errors = new HashMap<>();
        if (dto.getNom() == null || dto.getNom().isBlank()) errors.put("nom", "obligatoire");
        if (dto.getPrefecture() == null || dto.getPrefecture().isBlank()) errors.put("prefecture", "obligatoire");
        if (dto.getCommune() == null || dto.getCommune().isBlank()) errors.put("commune", "obligatoire");
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(Map.of("errors", errors));

        Douar saved = douarService.create(fromDto(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    /** Mise à jour (y compris géométrie si fournie) */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DouarDTO dto) {
        return douarService.findById(id).map(existing -> {
            if (dto.getNom() != null) existing.setNom(dto.getNom());
            if (dto.getStatut() != null) existing.setStatut(dto.getStatut());
            if (dto.getPrefecture() != null) existing.setPrefecture(dto.getPrefecture());
            if (dto.getCommune() != null) existing.setCommune(dto.getCommune());
            if (dto.getCreatedByUserId() != null) existing.setCreatedByUserId(dto.getCreatedByUserId());

            fillGeometryFromDto(dto, existing);

            Douar saved = douarService.update(existing);
            return ResponseEntity.ok(toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }



    /** Export GeoJSON (Leaflet-ready) */
    @GetMapping("/geojson")
    public Map<String, Object> geojson(
            @RequestParam(required = false) String prefecture,
            @RequestParam(required = false) String commune
    ) {
        List<Douar> all = douarService.findByLocation(prefecture, commune);

        List<Map<String, Object>> features = all.stream().map(d -> {
            Map<String, Object> f = new LinkedHashMap<>();
            f.put("type", "Feature");

            Map<String, Object> props = new LinkedHashMap<>();
            props.put("id", d.getDouarId());
            props.put("nom", d.getNom());
            props.put("prefecture", d.getPrefecture());
            props.put("commune", d.getCommune());
            props.put("statut", d.getStatut() != null ? d.getStatut().name() : null);
            f.put("properties", props);

            Polygon poly = d.getGeometry();
            if (poly != null) {
                Map<String, Object> geom = new LinkedHashMap<>();
                geom.put("type", "Polygon");
                // polygonToGeoJsonCoordinates = anneau extérieur ; wrapper en [ ring ]
                geom.put("coordinates", List.of(geometryUtils.polygonToGeoJsonCoordinates(poly)));
                f.put("geometry", geom);
            } else {
                f.put("geometry", null); // pas de géométrie -> Leaflet l'ignorera
            }
            return f;
        }).collect(Collectors.toList());

        Map<String, Object> fc = new LinkedHashMap<>();
        fc.put("type", "FeatureCollection");
        fc.put("features", features);
        return fc;
    }
}
