package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.PV;
import com.example.HNR.Repository.SqlServer.PVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PVServiceImpl implements PVService {

    @Autowired
    private PVRepository pvRepository;

    @Override
    public PV create(PV pv) {
        return pvRepository.save(pv);
    }

    @Override
    public Optional<PV> findById(Long id) {
        return pvRepository.findById(id);
    }

    @Override
    public List<PV> findAll() {
        return pvRepository.findAll();
    }

    @Override
    public PV update(PV pv) {
        return pvRepository.save(pv);
    }

    @Override
    public void delete(Long id) {
        pvRepository.deleteById(id);
    }



    @Override
    public List<PV> findByRedacteurUserId(String userId) {
        return pvRepository.findByRedacteurUserId(userId);
    }



    @Override
    public Optional<PV> findByActionId(Long actionId) {
        return pvRepository.findByActionActionId(actionId);
    }

    @Override
    public List<PV> findByDateRange(Date startDate, Date endDate) {
        return pvRepository.findByDateRedactionBetween(startDate, endDate);
    }

    @Override
    public List<PV> findPVsWithPDF() {
        return pvRepository.findPVsWithPDF();
    }

    @Override
    public void validerPV(Long id, String validateurUserId) {
        Optional<PV> pv = pvRepository.findById(id);
        if (pv.isPresent()) {
            pv.get().valider(validateurUserId);
            pvRepository.save(pv.get());
        }
    }
}