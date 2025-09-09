package com.example.HNR.Service;

import com.example.HNR.DTO.Request.DouarCreateRequest;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.StatutDouar;
import com.example.HNR.Repository.SqlServer.DouarRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.HNR.Repository.Mongodb.UserRepository;
import com.example.HNR.Model.Mongodb.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DouarServiceImpl implements DouarService {

    private final DouarRepository douarRepository;
    private final ObjectMapper objectMapper; // conservé si utilisé ailleurs
    private final UserRepository userRepository;

    /* ---------- create(Douar) ---------- */
    @Override
    @Transactional
    public Douar create(Douar douar) {
        if (douar.getStatut() == null) {
            douar.setStatut(StatutDouar.NON_ERADIQUE);
        }
        if (douar.getCreatedByUserId() == null) {
            String uid = currentUserId();
            if (uid != null) douar.setCreatedByUserId(uid);
        }
        // Sécurise la géométrie si fournie
        if (douar.getGeometry() != null) {
            Polygon poly = douar.getGeometry();
            poly.setSRID(4326);
            douar.setGeometry(fixIfInvalid(poly));
        }
        return douarRepository.save(douar);
    }

    /* Récupère l'userId (Mongo) à partir de l'email authentifié */
    private String currentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            String email = auth.getName(); // chez toi = email
            return userRepository.findByEmail(email).map(User::getUserid).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /* Conversion sûre String -> StatutDouar (null si invalide) */
    private StatutDouar parseStatut(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return StatutDouar.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /* ---------- création depuis GeoJSON ---------- */
    @Override
    @Transactional
    public Douar createFromGeoJson(DouarCreateRequest req) {
        try {
            // 1) Lire la geometry (GeoJSON) du DTO
            JsonNode geom = req.getGeometry();
            if (geom == null || geom.isNull()) {
                throw new IllegalArgumentException("geometry manquante");
            }
            String type = geom.get("type").asText(null);
            if (type == null || !"Polygon".equalsIgnoreCase(type)) {
                throw new IllegalArgumentException("La géométrie doit être un Polygon GeoJSON.");
            }

            // coordinates: [ [ [lng,lat], [lng,lat], ... ] , [ ... holes ... ]? ]
            JsonNode rings = geom.get("coordinates");
            if (rings == null || !rings.isArray() || rings.isEmpty()) {
                throw new IllegalArgumentException("coordinates invalides");
            }

            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);

            // ----- anneau extérieur -----
            LinearRing shell = toLinearRing(gf, rings.get(0));

            // ----- trous (optionnels) -----
            LinearRing[] holes = null;
            if (rings.size() > 1) {
                holes = new LinearRing[rings.size() - 1];
                for (int i = 1; i < rings.size(); i++) {
                    holes[i - 1] = toLinearRing(gf, rings.get(i));
                }
            }

            Polygon poly = gf.createPolygon(shell, holes);
            poly.setSRID(4326);
            poly = fixIfInvalid(poly); // auto-réparation si nécessaire

            // 2) Mapper DTO -> entité
            Douar d = new Douar();
            d.setNom(req.getNom());
            d.setPrefecture(req.getPrefecture());
            d.setCommune(req.getCommune());
            d.setGeometry(poly);

            // ➜ convertir la String reçue en enum (fallback NON_ERADIQUE)
            StatutDouar statut = parseStatut(req.getStatut());
            d.setStatut(statut != null ? statut : StatutDouar.NON_ERADIQUE);

            // 3) Renseigner le créateur à partir du SecurityContext
            String uid = currentUserId();
            if (uid != null) d.setCreatedByUserId(uid);

            // 4) Persist
            return douarRepository.save(d);

        } catch (Exception ex) {
            throw new RuntimeException("Échec de création du douar (GeoJSON) : " + ex.getMessage(), ex);
        }
    }

    /* -------- helper: JsonNode ring -> LinearRing (fermeture automatique) -------- */
    private LinearRing toLinearRing(GeometryFactory gf, JsonNode ringArray) {
        if (ringArray == null || !ringArray.isArray() || ringArray.size() < 4) {
            throw new IllegalArgumentException("Anneau polygonal invalide (au moins 4 points requis).");
        }
        int n = ringArray.size();

        // Vérifie/force la fermeture de l'anneau
        double firstLng = ringArray.get(0).get(0).asDouble();
        double firstLat = ringArray.get(0).get(1).asDouble();
        double lastLng  = ringArray.get(n - 1).get(0).asDouble();
        double lastLat  = ringArray.get(n - 1).get(1).asDouble();

        boolean closed = (Double.compare(firstLng, lastLng) == 0) && (Double.compare(firstLat, lastLat) == 0);

        int len = closed ? n : n + 1;
        Coordinate[] coords = new Coordinate[len];

        for (int i = 0; i < n; i++) {
            JsonNode pt = ringArray.get(i);
            double lng = pt.get(0).asDouble(); // x
            double lat = pt.get(1).asDouble(); // y
            coords[i] = new Coordinate(lng, lat);
        }
        if (!closed) {
            coords[len - 1] = coords[0];
        }
        return gf.createLinearRing(coords);
    }

    /* -------- helper: répare les polygones invalides via buffer(0) -------- */
    private Polygon fixIfInvalid(Polygon p) {
        if (p == null || p.isValid()) return p;
        Geometry fixed = p.buffer(0);
        if (fixed instanceof Polygon poly) {
            poly.setSRID(4326);
            return poly;
        }
        if (fixed instanceof MultiPolygon mp && mp.getNumGeometries() > 0) {
            Polygon poly = (Polygon) mp.getGeometryN(0);
            poly.setSRID(4326);
            return poly;
        }
        throw new IllegalArgumentException("Polygon invalide et irréparable");
    }

    /* ---------- le reste inchangé ---------- */

    @Override public Optional<Douar> findById(Long id) { return douarRepository.findById(id); }
    @Override public List<Douar> findAll() { return douarRepository.findAll(); }
    @Override public Page<Douar> findAll(Pageable pageable) { return douarRepository.findAll(pageable); }
    @Override @Transactional public Douar update(Douar douar) {
        // sécurise la géométrie à l'update également
        if (douar.getGeometry() != null) {
            Polygon poly = douar.getGeometry();
            poly.setSRID(4326);
            douar.setGeometry(fixIfInvalid(poly));
        }
        return douarRepository.save(douar);
    }

    @Override
    public List<Douar> findByLocation(String prefecture, String commune) {
        if (prefecture == null && commune == null) return douarRepository.findAll();
        if (prefecture != null && commune != null) return douarRepository.findByPrefectureAndCommune(prefecture, commune);
        if (prefecture != null) return douarRepository.findByPrefecture(prefecture);
        return douarRepository.findByCommune(commune);
    }

    @Override public Optional<Douar> findByNom(String nom) { return douarRepository.findByNom(nom); }
    @Override public List<Douar> findByStatut(StatutDouar statut) { return douarRepository.findByStatut(statut); }
    @Override public List<Douar> findByMissionId(Long missionId) { return douarRepository.findByMMissionId(missionId); }
    @Override public List<Douar> findByCreatedByUserId(String userId) { return douarRepository.findByCreatedByUserId(userId); }
    @Override public List<Douar> findActiveDouars() { return douarRepository.findActiveDouars(); }

    @Override
    @Transactional
    public void eradiquerDouar(Long id) {
        douarRepository.findById(id).ifPresent(d -> {
            d.eradiquer();
            douarRepository.save(d);
        });
    }
}
