package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.StatutDouar;
import com.example.HNR.Repository.SqlServer.DouarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DouarServiceImpl implements DouarService {

    @Autowired
    private DouarRepository douarRepository;

    @Override
    public Douar create(Douar douar) {
        return douarRepository.save(douar);
    }

    @Override
    public Optional<Douar> findById(Long id) {
        return douarRepository.findById(id);
    }

    @Override
    public List<Douar> findAll() {
        return douarRepository.findAll();
    }

    @Override
    public Douar update(Douar douar) {
        return douarRepository.save(douar);
    }

    @Override
    public void delete(Long id) {
        Optional<Douar> douar = douarRepository.findById(id);
        if (douar.isPresent()) {
            douar.get().softDelete();
            douarRepository.save(douar.get());
        }
    }

    @Override
    public Optional<Douar> findByNom(String nom) {
        return douarRepository.findByNom(nom);
    }

    @Override
    public List<Douar> findByStatut(StatutDouar statut) {
        return douarRepository.findByStatut(statut);
    }

    @Override
    public List<Douar> findByLocation(String prefecture, String commune) {
        return douarRepository.findByPrefectureAndCommune(prefecture, commune);
    }

    @Override
    public List<Douar> findByMissionId(Long missionId) {
        return douarRepository.findByMissionMissionId(missionId);
    }

    @Override
    public List<Douar> findByCreatedByUserId(String userId) {
        return douarRepository.findByCreatedByUserId(userId);
    }

    @Override
    public List<Douar> findActiveDouars() {
        return douarRepository.findActiveDouars();
    }

    @Override
    public void eradiquerDouar(Long id) {
        Optional<Douar> douar = douarRepository.findById(id);
        if (douar.isPresent()) {
            douar.get().eradiquer();
            douarRepository.save(douar.get());
        }
    }
}