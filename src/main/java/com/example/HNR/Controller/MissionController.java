package com.example.HNR.Controller;

import com.example.HNR.Model.*;
import com.example.HNR.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/missions")
@CrossOrigin(origins = "*")
public class MissionController {
    @Autowired private MissionRepository repo;
    @Autowired
    private DouarRepository douarRepository;

    @GetMapping("/{id}/douars")
    public List<Douar> getDouarsByMission(@PathVariable String id) {
        return douarRepository.findByIdMission(id);
    }

    @GetMapping public List<Mission> all() { return repo.findAll(); }
    @GetMapping("/{id}") public Optional<Mission> byId(@PathVariable String id){ return repo.findById(id);}
    @PostMapping public Mission add(@RequestBody Mission m){ return repo.save(m);}
    @PutMapping("/{id}") public Mission update(@PathVariable String id,@RequestBody Mission m){ m.setId(id); return repo.save(m);}
    @DeleteMapping("/{id}") public void delete(@PathVariable String id){ repo.deleteById(id);}
}
