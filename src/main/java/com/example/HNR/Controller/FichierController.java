package com.example.HNR.Controller;

import com.example.HNR.Model.SqlServer.Fichier;
import com.example.HNR.Service.FichierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fichiers")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
public class FichierController {

    @Autowired
    private FichierService fichierService;

    // GET all fichiers - accessible à tous les rôles authentifiés
    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getAllFichiers() {
        try {
            List<Fichier> fichiers = fichierService.findAll();
            return new ResponseEntity<>(fichiers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET fichier by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Fichier> getFichierById(@PathVariable Long id) {
        try {
            Optional<Fichier> fichier = fichierService.findById(id);
            if (fichier.isPresent()) {
                return new ResponseEntity<>(fichier.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST create fichier - accessible à tous les rôles authentifiés
    @PostMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<Fichier> createFichier(@RequestBody Fichier fichier) {
        try {
            // Validation basique
            if (fichier.getFileName() == null || fichier.getFileName().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (fichier.getFilePath() == null || fichier.getUrl() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (fichier.getUploadedByUserId() == null || fichier.getUploadedByUserId().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Définir la date d'upload si non fournie
            if (fichier.getUploadedAt() == null) {
                fichier.setUploadedAt(new Date());
            }

            // S'assurer que le fichier n'est pas marqué comme supprimé à la création
            fichier.setDeletedAt(null);

            Fichier savedFichier = fichierService.create(fichier);
            return new ResponseEntity<>(savedFichier, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update fichier - uploader + GOUVERNEUR/MEMBRE_DSI
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@fichierServiceImpl.findById(#id).isPresent() and @fichierServiceImpl.findById(#id).get().getUploadedByUserId() == authentication.name)")
    public ResponseEntity<Fichier> updateFichier(@PathVariable Long id, @RequestBody Fichier fichier) {
        try {
            Optional<Fichier> existingFichier = fichierService.findById(id);
            if (existingFichier.isPresent()) {
                fichier.setFichierId(id);
                // Conserver les métadonnées originales
                fichier.setUploadedAt(existingFichier.get().getUploadedAt());
                fichier.setDeletedAt(existingFichier.get().getDeletedAt());

                Fichier updatedFichier = fichierService.update(fichier);
                return new ResponseEntity<>(updatedFichier, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE fichier (soft delete) - uploader + GOUVERNEUR/MEMBRE_DSI
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@fichierServiceImpl.findById(#id).isPresent() and @fichierServiceImpl.findById(#id).get().getUploadedByUserId() == authentication.name)")
    public ResponseEntity<Void> deleteFichier(@PathVariable Long id) {
        try {
            Optional<Fichier> fichier = fichierService.findById(id);
            if (fichier.isPresent()) {
                fichierService.delete(id); // Effectue un soft delete
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET fichiers by content type
    @GetMapping("/content-type/{contentType}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getFichiersByContentType(@PathVariable String contentType) {
        try {
            List<Fichier> fichiers = fichierService.findByContentType(contentType);
            return new ResponseEntity<>(fichiers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET fichiers by uploader user ID
    @GetMapping("/uploaded-by/{userId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #userId == authentication.name")
    public ResponseEntity<List<Fichier>> getFichiersByUploader(@PathVariable String userId) {
        try {
            List<Fichier> fichiers = fichierService.findByUserId(userId);
            return new ResponseEntity<>(fichiers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET fichiers by changement ID
    @GetMapping("/changement/{changementId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getFichiersByChangementId(@PathVariable Long changementId) {
        try {
            List<Fichier> fichiers = fichierService.findByChangementId(changementId);
            return new ResponseEntity<>(fichiers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET fichiers by entity
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getFichiersByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        try {
            List<Fichier> fichiers = fichierService.findByEntity(entityType, entityId);
            return new ResponseEntity<>(fichiers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET images only
    @GetMapping("/images")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getImages() {
        try {
            List<Fichier> images = fichierService.findImages();
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET active fichiers (non supprimés)
    @GetMapping("/active")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getActiveFichiers() {
        try {
            List<Fichier> fichiers = fichierService.findActiveFichiers();
            return new ResponseEntity<>(fichiers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET fichiers by current user (uploadés par l'utilisateur connecté)
    @GetMapping("/my-fichiers")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getMyFichiers() {
        try {
            String currentUserId = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();

            List<Fichier> fichiers = fichierService.findByUserId(currentUserId);
            return new ResponseEntity<>(fichiers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET PDFs only
    @GetMapping("/pdfs")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getPDFs() {
        try {
            List<Fichier> pdfs = fichierService.findByContentType("application/pdf");
            return new ResponseEntity<>(pdfs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET recent fichiers (ordre décroissant par date d'upload)
    @GetMapping("/recent")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getRecentFichiers() {
        try {
            List<Fichier> fichiers = fichierService.findActiveFichiers();
            // Trier par date d'upload décroissante
            fichiers.sort((f1, f2) -> f2.getUploadedAt().compareTo(f1.getUploadedAt()));

            // Limiter aux 20 plus récents
            List<Fichier> recentFichiers = fichiers.stream().limit(20).toList();

            return new ResponseEntity<>(recentFichiers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT restore deleted fichier - uploader + GOUVERNEUR/MEMBRE_DSI
    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@fichierServiceImpl.findById(#id).isPresent() and @fichierServiceImpl.findById(#id).get().getUploadedByUserId() == authentication.name)")
    public ResponseEntity<Fichier> restoreFichier(@PathVariable Long id) {
        try {
            Optional<Fichier> existingFichier = fichierService.findById(id);
            if (existingFichier.isPresent()) {
                Fichier fichier = existingFichier.get();
                if (!fichier.isDeleted()) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT); // Déjà actif
                }

                fichier.setDeletedAt(null); // Restaurer
                Fichier restoredFichier = fichierService.update(fichier);
                return new ResponseEntity<>(restoredFichier, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update file metadata
    @PutMapping("/{id}/metadata")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@fichierServiceImpl.findById(#id).isPresent() and @fichierServiceImpl.findById(#id).get().getUploadedByUserId() == authentication.name)")
    public ResponseEntity<Fichier> updateFichierMetadata(
            @PathVariable Long id,
            @RequestBody FileMetadataUpdateRequest request) {
        try {
            Optional<Fichier> existingFichier = fichierService.findById(id);
            if (existingFichier.isPresent()) {
                Fichier fichier = existingFichier.get();

                if (request.getEntityType() != null) {
                    fichier.setEntityType(request.getEntityType());
                }
                if (request.getEntityId() != null) {
                    fichier.setEntityId(request.getEntityId());
                }
                if (request.getFileName() != null) {
                    fichier.setFileName(request.getFileName());
                }

                Fichier updatedFichier = fichierService.update(fichier);
                return new ResponseEntity<>(updatedFichier, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET fichiers by file extension
    @GetMapping("/extension/{extension}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<Fichier>> getFichiersByExtension(@PathVariable String extension) {
        try {
            List<Fichier> allFichiers = fichierService.findActiveFichiers();
            List<Fichier> filteredFichiers = allFichiers.stream()
                    .filter(f -> f.getFileName().toLowerCase().endsWith("." + extension.toLowerCase()))
                    .toList();

            return new ResponseEntity<>(filteredFichiers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Classe interne pour la mise à jour des métadonnées
    public static class FileMetadataUpdateRequest {
        private String entityType;
        private Long entityId;
        private String fileName;

        public FileMetadataUpdateRequest() {}

        public FileMetadataUpdateRequest(String entityType, Long entityId, String fileName) {
            this.entityType = entityType;
            this.entityId = entityId;
            this.fileName = fileName;
        }

        // Getters et Setters
        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }

        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
    }
}