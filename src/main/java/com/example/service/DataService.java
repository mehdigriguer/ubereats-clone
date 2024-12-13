package com.example.service;

import com.example.model.Courier;
import com.example.repository.CourierRepository;
import org.apache.coyote.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {
    private final CourierRepository courierRepository;

    public DataService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    public Courier addCourier(Courier courier) {
        return courierRepository.save(courier);
    }

    public List<Courier> getAllCouriers() {
        return courierRepository.findAll();
    }
}
