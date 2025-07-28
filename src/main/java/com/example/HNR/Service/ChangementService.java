package com.example.HNR.Service;

import com.example.HNR.Model.Changement;
import com.example.HNR.repository.ChangementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChangementService {

    @Autowired
    private ChangementRepository changementRepository;

    public List<Changement> getAllChangements() {
        return changementRepository.findAll();
    }

    public Optional<Changement> getChangementById(String id) {
        return changementRepository.findById(id);
    }

    public Changement saveChangement(Changement changement) {
        return changementRepository.save(changement);
    }

    public void deleteChangement(String id) {
        changementRepository.deleteById(id);
    }

    // Méthode personnalisée si besoin : lister par douar
    public List<Changement> getChangementsByDouarId(String douarId) {
        return changementRepository.findByIdDouar(douarId);
    }
    public Changement getChangementByCode(String code) {
        return changementRepository.findByCodeChangement(code);
    }
}
