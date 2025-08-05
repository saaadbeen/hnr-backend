package com.example.HNR.Controller;

import com.example.HNR.Model.Fichier;
import com.example.HNR.Service.FichierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fichiers")
@CrossOrigin(origins = "*")
public class FichierController {

    @Autowired
    private FichierService fichierService;

    // GET /api/fichiers - Obtenir tous les fichiers
    @GetMapping
    public ResponseEntity<List<Fichier>> getAllFichiers() {
        List<Fichier> fichiers = fichierService.findAll();
        return ResponseEntity.ok(fichiers);
    }

    // GET /api/fichiers/{id} - Obtenir fichier par ID
    @GetMapping("/{id}")
    public ResponseEntity<Fichier> getFichierById(@PathVariable String id) {
        Optional<Fichier> fichier = fichierService.findById(id);
        if (fichier.isPresent()) {
            return ResponseEntity.ok(fichier.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/fichiers/search/{nom} - Rechercher fichiers par nom
    @GetMapping("/search/{nom}")
    public ResponseEntity<List<Fichier>> searchFichiersByName(@PathVariable String nom) {
        List<Fichier> fichiers = fichierService.searchByName(nom);
        return ResponseEntity.ok(fichiers);
    }

    // POST /api/fichiers/upload - Upload un fichier
    @PostMapping("/upload")
    public ResponseEntity<Fichier> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") String entityId,
            @RequestParam("uploadedBy") String uploadedBy) {
        try {
            Fichier uploadedFichier = fichierService.uploadFile(file, entityType, entityId, uploadedBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFichier);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/fichiers/{id} - Supprimer fichier
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFichier(@PathVariable String id) {
        try {
            fichierService.deleteFile(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
