package com.example.HNR.Controller;

import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.SqlServer.Mission;
import com.example.HNR.Service.DouarService;
import com.example.HNR.Service.MissionService;
import com.example.HNR.Util.GeometryUtils;
import org.locationtech.jts.geom.Polygon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carte")
public class CarteController {

    private final DouarService douarService;
    private final MissionService missionService;
    private final GeometryUtils geometryUtils;

    public CarteController(DouarService d, MissionService m, GeometryUtils g) {
        this.douarService = d;
        this.missionService = m;
        this.geometryUtils = g;
    }

    @GetMapping("/donnees")
    public ResponseEntity<Map<String, Object>> getDonneesCartographiques() {
        Map<String, Object> out = new HashMap<>();

        List<Map<String, Object>> douarFeatures = douarService.findAll().stream()
                .filter(d -> d.getGeometry() != null)
                .map(this::douarToFeature)
                .collect(Collectors.toList());

        List<Map<String, Object>> missionFeatures = missionService.findAll().stream()
                .map(this::missionToFeatureSafely)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        out.put("douars", featureCollection(douarFeatures));
        out.put("missions", featureCollection(missionFeatures));
        out.put("bbox", defaultBbox());

        return ResponseEntity.ok(out);
    }

    private Map<String, Object> douarToFeature(Douar d) {
        Map<String, Object> f = new HashMap<>();
        f.put("type", "Feature");
        f.put("id", d.getDouarId());

        Map<String, Object> props = new HashMap<>();
        props.put("nom", d.getNom());
        props.put("statut", String.valueOf(d.getStatut()));
        props.put("commune", d.getCommune());
        props.put("prefecture", d.getPrefecture());
        props.put("type", "douar");
        f.put("properties", props);

        Polygon poly = d.getGeometry();
        Map<String, Object> geom = new HashMap<>();
        geom.put("type", "Polygon");
        geom.put("coordinates", List.of(geometryUtils.polygonToGeoJsonCoordinates(poly)));
        f.put("geometry", geom);

        return f;
    }

    private Map<String, Object> missionToFeatureSafely(Mission m) {
        try {
            Polygon poly = null;


            Map<String, Object> f = new HashMap<>();
            f.put("type", "Feature");
            f.put("id", m.getMissionId());

            Map<String, Object> props = new HashMap<>();
            props.put("titre", m.getTitre());
            props.put("statut", m.getStatut());
            props.put("missionType", m.getGeometryType());
            props.put("commune", m.getCommune());
            props.put("prefecture", m.getPrefecture());
            props.put("type", "mission");
            f.put("properties", props);

            Map<String, Object> geom = new HashMap<>();
            geom.put("type", "Polygon");
            geom.put("coordinates", List.of(geometryUtils.polygonToGeoJsonCoordinates(poly)));
            f.put("geometry", geom);
            return f;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Map<String, Object> featureCollection(List<Map<String, Object>> features) {
        Map<String, Object> fc = new HashMap<>();
        fc.put("type", "FeatureCollection");
        fc.put("features", features);
        return fc;
    }

    private Map<String, Object> defaultBbox() {
        return Map.of("south", 33.54, "west", -7.70, "north", 33.70, "east", -7.35);
    }
}
