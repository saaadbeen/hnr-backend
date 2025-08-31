package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Changement;
import com.example.HNR.Model.enums.TypeExtension;
import com.example.HNR.Repository.SqlServer.ChangementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChangementServiceImpl implements ChangementService {

    @Autowired
    private ChangementRepository changementRepository;

    @Override
    public Changement create(Changement changement) {
        return changementRepository.save(changement);
    }

    @Override
    public Optional<Changement> findById(Long id) {
        return changementRepository.findById(id);
    }

    @Override
    public List<Changement> findAll() {
        return changementRepository.findAll();
    }

    @Override
    public Changement update(Changement changement) {
        return changementRepository.save(changement);
    }

    @Override
    public void delete(Long id) {
        changementRepository.deleteById(id);
    }

    @Override
    public List<Changement> findByType(TypeExtension type) {
        return changementRepository.findByType(type);
    }

    @Override
    public List<Changement> findByDouarId(Long douarId) {
        return changementRepository.findByDouarDouarId(douarId);
    }

    @Override
    public List<Changement> findByDetectedByUserId(String userId) {
        return changementRepository.findByDetectedByUserId(userId);
    }

    @Override
    public List<Changement> findByDateRange(Date startDate, Date endDate) {
        return changementRepository.findByDateBeforeBetween(startDate, endDate);
    }



    @Override
    public List<Changement> findBySurfaceMinimum(Double minSurface) {
        return changementRepository.findBySurfaceGreaterThanEqual(minSurface);
    }
}