package com.example.HNR.Service;

import com.example.HNR.Model.Fichier;
import com.example.HNR.Repository.FichierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FichierService {

    @Autowired
    private FichierRepository fichierRepository;

    private final String uploadDir = "uploads/";

    // Upload un fichier
    public Fichier uploadFile(MultipartFile file, String entityType, String entityId, String uploadedBy) {
        try {
            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom unique
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Sauvegarder le fichier
            Files.copy(file.getInputStream(), filePath);

            // Créer l'entité Fichier
            Fichier fichier = new Fichier();
            fichier.setNomFichier(file.getOriginalFilename());
            fichier.setFilePath(filePath.toString());
            fichier.setFileType(file.getContentType());
            fichier.setFileSize(file.getSize());
            fichier.setDateupload(new Date());
            fichier.setUploadedBy(uploadedBy);
            fichier.setEntityType(entityType);
            fichier.setEntityId(entityId);

            return fichierRepository.save(fichier);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier: " + e.getMessage());
        }
    }

    // Trouver par ID
    public Optional<Fichier> findById(String id) {
        return fichierRepository.findById(id);
    }

    // Obtenir tous les fichiers
    public List<Fichier> findAll() {
        return fichierRepository.findAll();
    }

    // Recherche par nom
    public List<Fichier> searchByName(String nom) {
        return fichierRepository.findByNomFichierContainingIgnoreCase(nom);
    }

    // Supprimer un fichier
    public void deleteFile(String id) {
        Fichier fichier = fichierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));

        try {
            // Supprimer le fichier physique
            Files.deleteIfExists(Paths.get(fichier.getFilePath()));

            // Supprimer de la base de données
            fichierRepository.delete(fichier);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression du fichier: " + e.getMessage());
        }
    }
}