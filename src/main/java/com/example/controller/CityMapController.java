package com.example.controller;

import com.example.model.CityMap;
import com.example.service.CityMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/city-map")
public class    CityMapController {

    @Autowired
    private CityMapService cityMapService;

    /**
     * Endpoint pour charger une CityMap à partir d'un fichier XML.
     * @param filePath Chemin absolu vers le fichier XML
     * @return La CityMap ou une erreur en cas de problème
     */

    @GetMapping("/loadmap")
    public ResponseEntity<?> getCityMap() {
        try {
            // filepath a mettre par defaut
            // Charger la CityMap à partir du fichier spécifié
            CityMap cityMap = cityMapService.loadFromXML("src/main/resources/fichiersXMLPickupDelivery/petitPlan.xml");
            return ResponseEntity.ok(cityMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading city map from file: " + e.getMessage());
        }
    }
}
