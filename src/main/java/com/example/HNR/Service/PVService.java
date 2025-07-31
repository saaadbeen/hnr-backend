package com.example.HNR.Service;

import com.example.HNR.Model.PV;
import com.example.HNR.Repository.PVRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class PVService {

    @Autowired
    private PVRepository pvRepository;

    public PV createPv(PV pv) {
        return pvRepository.save(pv);
    }

    public List<PV> getAllPvs() {
        return pvRepository.findAll();
    }

    public PV getPvById(String id) {
        return pvRepository.findById(id).orElse(null);
    }

    public PV updatePv(String id, PV updated) {
        Optional<PV> optional = pvRepository.findById(id);
        if (optional.isPresent()) {
            updated.setId(id);
            return pvRepository.save(updated);
        } else {
            return null;
        }
    }

    public boolean deletePv(String id) {
        if (pvRepository.existsById(id)) {
            pvRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 🔍 Find
    public List<PV> findByMissionId(String missionId) {
        return pvRepository.findByMissionId(missionId);
    }

    public List<PV> findByDouarId(String douarId) {
        return pvRepository.findByIdDouar(douarId);
    }

    public List<PV> findByRedacteurId(String redacteurId) {
        return pvRepository.findByRedacteurId(redacteurId);
    }
}