package com.example.HNR.Service;
import java.util.Optional;

import com.example.HNR.Model.Utilisateur;
import com.example.HNR.Repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/* Service métier pour les utilisateurs : CRUD + recherche par email ou UUID métier. */
@Service
@RequiredArgsConstructor      // injection par constructeur (Lombok)
public class UtilisateurService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur getUtilisateurById(String id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    public Utilisateur updateUtilisateur(String id, Utilisateur updated) {
        Optional<Utilisateur> optional = utilisateurRepository.findById(id);
        if (optional.isPresent()) {
            updated.setId(id);
            return utilisateurRepository.save(updated);
        } else {
            return null;
        }
    }

    public boolean deleteUtilisateur(String id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 🔍 Find
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElse(null);
    }
}