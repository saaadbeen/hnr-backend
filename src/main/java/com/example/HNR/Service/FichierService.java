package com.example.HNR.Service;

import com.example.HNR.Model.Fichier;
import com.example.HNR.repository.FichierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FichierService {

    @Autowired
    private FichierRepository fichierRepository;

    public List<Fichier> getAllFichiers() {
        return fichierRepository.findAll();
    }

    public Optional<Fichier> getFichierById(String id) {
        return fichierRepository.findById(id);
    }

    public Fichier saveFichier(Fichier fichier) {
        return fichierRepository.save(fichier);
    }

    public void deleteFichier(String id) {
        fichierRepository.deleteById(id);
    }

    // Requête personnalisée : récupérer les fichiers d’un douar
    public List<Fichier> getFichiersByDouarId(String douarId) {
        return fichierRepository.findByIdDouar(douarId);
    }
}
