package com.example.HNR.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;                 // ✅ attention à l'import
import org.springframework.core.io.UrlResource;          // ✅
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/fichiers")
@Slf4j
public class FichierController {

    /** Dossier de stockage (configurable via application.yml: app.upload-dir: uploads) */
    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    /** Sauvegarde un fichier et renvoie les URLs (affichage + download). */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "entity", required = false) String entity,
            @RequestParam(value = "entityId", required = false) String entityId
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Fichier manquant"));
        }

        // Création du dossier si besoin
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root);

        // Nom de fichier sécurisé + unique
        String original = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "file.bin"));
        String safeBase = original.replace("\\", "_").replace("/", "_");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String stored = ts + "_" + UUID.randomUUID() + "_" + safeBase;

        Path target = root.resolve(stored).normalize();
        // Évite toute évasion de chemin
        if (!target.startsWith(root)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nom de fichier invalide"));
        }

        // Copie
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Construit des URLs publiques relatives à l'API
        String base = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/fichiers")
                .toUriString();

        String rawUrl = base + "/raw/" + stored;
        String downloadUrl = base + "/download/" + stored;

        Map<String, Object> body = new HashMap<>();
        body.put("filename", stored);
        body.put("originalName", original);
        body.put("size", file.getSize());
        body.put("contentType", Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE));
        body.put("url", rawUrl);              // 👈 ton front lit d’abord "url"
        body.put("downloadUrl", downloadUrl); // … ou "downloadUrl"
        body.put("path", stored);             // … ou "path"
        body.put("entity", entity);
        body.put("entityId", entityId);

        return ResponseEntity.ok(body);
    }

    /** Affiche le fichier (Content-Disposition: inline). Utile pour PDF/Images. */
    @GetMapping("/raw/{filename:.+}")
    public ResponseEntity<Resource> serveInline(@PathVariable String filename) throws IOException {
        Resource res = loadAsResource(filename);
        if (res == null || !res.exists()) return ResponseEntity.notFound().build();

        MediaType type = probeContentType(res);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(res.getFilename()))
                .contentType(type)
                .body(res);
    }

    /** Télécharge le fichier (Content-Disposition: attachment). */
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> download(@PathVariable String filename) throws IOException {
        Resource res = loadAsResource(filename);
        if (res == null || !res.exists()) return ResponseEntity.notFound().build();

        MediaType type = probeContentType(res);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionAttachment(res.getFilename()))
                .contentType(type)
                .body(res);
    }

    // ===================== helpers =====================

    private Resource loadAsResource(String filename) throws MalformedURLException {
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path path = root.resolve(filename).normalize();
        if (!path.startsWith(root)) return null; // protection path traversal
        return new UrlResource(path.toUri());
    }

    private MediaType probeContentType(Resource res) throws IOException {
        try {
            Path p = Paths.get(res.getURI());
            String ct = Files.probeContentType(p);
            return (ct != null) ? MediaType.parseMediaType(ct) : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private String contentDispositionInline(String filename) {
        return "inline; filename=\"" + Optional.ofNullable(filename).orElse("file") + "\"";
    }

    private String contentDispositionAttachment(String filename) {
        return "attachment; filename=\"" + Optional.ofNullable(filename).orElse("file") + "\"";
    }
}
