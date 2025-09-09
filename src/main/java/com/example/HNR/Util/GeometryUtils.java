package com.example.HNR.Util;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GeometryUtils {

    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    /** Polygon -> anneau extérieur GeoJSON [[lng,lat], ...] */
    public List<List<Double>> polygonToGeoJsonCoordinates(Polygon polygon) {
        if (polygon == null) return null;
        Coordinate[] cs = polygon.getExteriorRing().getCoordinates();
        List<List<Double>> coords = new ArrayList<>(cs.length);
        for (Coordinate c : cs) coords.add(List.of(c.x, c.y)); // x=lng, y=lat
        return coords;
    }

    /** GeoJSON coords [[lng,lat],...] -> Polygon (ferme l'anneau si besoin) + auto-fix */
    public Polygon coordsToPolygon(List<List<Double>> coordinates) {
        if (coordinates == null || coordinates.size() < 4)
            throw new IllegalArgumentException("Polygon needs at least 4 coordinates (closed ring)");

        List<Coordinate> ring = new ArrayList<>();
        for (List<Double> p : coordinates) {
            if (p == null || p.size() < 2) throw new IllegalArgumentException("Invalid coordinate [lng,lat]");
            ring.add(new Coordinate(p.get(0), p.get(1))); // X=lng, Y=lat
        }
        if (!ring.get(0).equals2D(ring.get(ring.size()-1))) ring.add(new Coordinate(ring.get(0)));

        LinearRing shell = GF.createLinearRing(ring.toArray(Coordinate[]::new));
        Polygon poly = GF.createPolygon(shell, null);
        poly.setSRID(4326);
        return fixIfInvalid(poly);
    }

    /** WKT -> Polygon (accepte POLYGON ou MULTIPOLYGON) + auto-fix */
    public Polygon wktToPolygon(String wkt) {
        try {
            if (wkt == null || wkt.isBlank()) return null;
            Geometry g = new WKTReader(GF).read(wkt);
            g.setSRID(4326);
            Polygon p;
            if (g instanceof Polygon gp) {
                p = gp;
            } else if (g instanceof MultiPolygon mp && mp.getNumGeometries() > 0) {
                p = (Polygon) mp.getGeometryN(0);
                p.setSRID(4326);
            } else {
                throw new IllegalArgumentException("WKT must be POLYGON or MULTIPOLYGON");
            }
            return fixIfInvalid(p);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid WKT polygon: " + e.getMessage(), e);
        }
    }

    /** Centroid [lng,lat] */
    public double[] centroidLonLat(Polygon p) {
        if (p == null) return null;
        Point c = p.getCentroid();
        return new double[]{ c.getX(), c.getY() }; // X=lng, Y=lat
    }

    /** Corrige géométrie invalide (self-intersections…) via buffer(0) */
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
        throw new IllegalArgumentException("Polygon not valid and cannot be repaired");
    }
}
