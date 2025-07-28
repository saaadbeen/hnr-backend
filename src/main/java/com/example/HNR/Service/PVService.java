package com.example.HNR.Service;

import com.example.HNR.Model.PV;
import com.example.HNR.repository.PVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PVService {

    @Autowired
    private PVRepository pvRepository;

    public List<PV> getAllPVs() {
        return pvRepository.findAll();
    }

    public Optional<PV> getPVById(String id) {
        return pvRepository.findById(id);
    }

    public PV savePV(PV pv) {
        return pvRepository.save(pv);
    }

    public void deletePV(String id) {
        pvRepository.deleteById(id);
    }

    // Méthode personnalisée : lister les PV pour un douar
    public List<PV> getPVsByDouarId(String douarId) {
        return pvRepository.findByIdDouar(douarId);
    }
}
