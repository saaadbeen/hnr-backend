package com.example.HNR.Service;

import com.example.HNR.Model.Douar;
import com.example.HNR.repository.DouarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DouarService {

    @Autowired
    private DouarRepository douarRepository;

    public List<Douar> getAllDouars() {
        return douarRepository.findAll();
    }

    public Optional<Douar> getDouarById(String id) {
        return douarRepository.findById(id);
    }

    public Douar saveDouar(Douar douar) {
        return douarRepository.save(douar);
    }

    public void deleteDouar(String id) {
        douarRepository.deleteById(id);
    }
}
