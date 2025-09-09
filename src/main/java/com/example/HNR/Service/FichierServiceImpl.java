package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Fichier;
import com.example.HNR.Repository.SqlServer.FichierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FichierServiceImpl implements FichierService {
    private static final Logger log = LoggerFactory.getLogger(FichierServiceImpl.class);

    @Value("${app.upload.root:uploads}")
    private String rootDir;

    @Override
    public String storeActionPhoto(Long actionId, String kind, MultipartFile file) {
        try {
            String safeKind = ("apres".equalsIgnoreCase(kind)) ? "apres" : "avant";
            String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String filename = System.currentTimeMillis() + "_" + original;

            Path target = Paths.get(rootDir, "actions", String.valueOf(actionId), safeKind, filename)
                    .toAbsolutePath().normalize();
            Files.createDirectories(target.getParent());
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            // URL publique servie par WebConfig (/files/**)
            String url = "/files/actions/" + actionId + "/" + safeKind + "/" + filename;
            log.info("Photo enregistree: {}", url);
            return url;
        } catch (Exception ex) {
            log.error("Echec upload photo action {} kind {}", actionId, kind, ex);
            throw new RuntimeException("Impossible d'enregistrer le fichier");
        }
    }

    @Autowired
    private FichierRepository fichierRepository;

    @Override
    public Fichier create(Fichier fichier) {
        return fichierRepository.save(fichier);
    }

    @Override
    public Optional<Fichier> findById(Long id) {
        return fichierRepository.findById(id);
    }

    @Override
    public List<Fichier> findAll() {
        return fichierRepository.findAll();
    }

    @Override
    public Fichier update(Fichier fichier) {
        return fichierRepository.save(fichier);
    }

    @Override
    public void delete(Long id) {
        Optional<Fichier> fichier = fichierRepository.findById(id);
        if (fichier.isPresent()) {
            fichier.get().softDelete();
            fichierRepository.save(fichier.get());
        }
    }

    @Override
    public List<Fichier> findByContentType(String contentType) {
        return fichierRepository.findByContentType(contentType);
    }

    @Override
    public List<Fichier> findByUserId(String userId) {
        return fichierRepository.findByUploadedByUserId(userId);
    }

    @Override
    public List<Fichier> findByChangementId(Long changementId) {
        return fichierRepository.findByChangementChangementId(changementId);
    }

    @Override
    public List<Fichier> findByEntity(String entityType, Long entityId) {
        return fichierRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @Override
    public List<Fichier> findImages() {
        return fichierRepository.findImages();
    }

    @Override
    public List<Fichier> findActiveFichiers() {
        return fichierRepository.findActiveFichiers();
    }
}

