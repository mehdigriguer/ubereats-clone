package com.example.service;


import com.example.model.Reseau;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ReseauService {


    private final List<Reseau> reseaux = new ArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);


    public void save(Reseau reseau) {
        if (reseau.getId() == null) {
            reseau.setId(idGenerator.getAndIncrement());
        }
        reseaux.add(reseau);
    }


    public List<Reseau> findAll() {
        return new ArrayList<>(reseaux);
    }


    public Optional<Reseau> findById(Integer id) {
        return reseaux.stream().filter(r -> r.getId().equals(id)).findFirst();
    }


    public boolean deleteById(Integer id) {
        return reseaux.removeIf(r -> r.getId().equals(id));
    }
}
