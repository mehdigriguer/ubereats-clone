package com.example.controller;

import com.example.model.Reseau;
import com.example.service.ReseauService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reseaux")
public class ReseauController {

    private final ReseauService reseauService;

    public ReseauController(ReseauService reseauService) {
        this.reseauService = reseauService;
    }


    @PostMapping
    public void createReseau(@RequestBody Reseau reseau) {
        reseauService.save(reseau);
    }


    @GetMapping
    public List<Reseau> getAllReseaux() {
        return reseauService.findAll();
    }


    @GetMapping("/{id}")
    public Reseau getReseauById(@PathVariable Integer id) {
        return reseauService.findById(id).orElseThrow(() -> new RuntimeException("Réseau introuvable"));
    }


    @DeleteMapping("/{id}")
    public void deleteReseau(@PathVariable Integer id) {
        if (!reseauService.deleteById(id)) {
            throw new RuntimeException("Réseau introuvable");
        }
    }
}
