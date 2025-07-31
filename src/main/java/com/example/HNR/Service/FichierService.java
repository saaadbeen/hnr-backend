package com.example.HNR.Service;

import com.example.HNR.Model.Fichier;
import com.example.HNR.Repository.FichierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FichierService {

    @Autowired
    private FichierRepository fichierRepository;

    public Fichier createFichier(Fichier fichier) {
        return fichierRepository.save(fichier);
    }

    public List<Fichier> getAllFichiers() {
        return fichierRepository.findAll();
    }

    public Fichier getFichierById(String id) {
        return fichierRepository.findById(id).orElse(null);
    }

    public Fichier updateFichier(String id, Fichier updated) {
        Optional<Fichier> optional = fichierRepository.findById(id);
        if (optional.isPresent()) {
            updated.setId(id);
            return fichierRepository.save(updated);
        } else {
            return null;
        }
    }

    public boolean deleteFichier(String id) {
        if (fichierRepository.existsById(id)) {
            fichierRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 🔍 Find
    public List<Fichier> findByMissionId(String missionId) {
        return fichierRepository.findByMissionId(missionId);
    }

    public List<Fichier> findByChangementId(String changementId) {
        return fichierRepository.findByChangementId(changementId);
    }
}
