package com.example.HNR.Service;

import com.example.HNR.Model.Douar;
import com.example.HNR.Repository.DouarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

/* Service métier pour les douars. */
@Service
@RequiredArgsConstructor
public class DouarService {
    @Autowired
    private DouarRepository douarRepository;

    public Douar createDouar(Douar douar) {
        return douarRepository.save(douar);
    }

    public List<Douar> getAllDouars() {
        return douarRepository.findAll();
    }

    public Douar getDouarById(String id) {
        return douarRepository.findById(id).orElse(null);
    }

    public Douar updateDouar(String id, Douar updated) {
        Optional<Douar> optional = douarRepository.findById(id);
        if (optional.isPresent()) {
            updated.setId(id);
            return douarRepository.save(updated);
        } else {
            return null;
        }
    }

    public boolean deleteDouar(String id) {
        if (douarRepository.existsById(id)) {
            douarRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 🔍 Find
    public List<Douar> findByMissionId(String missionId) {
        return douarRepository.findByMissionIdsContaining(missionId);
    }

}
