package com.example.controller;

import com.example.model.CityMap;
import com.example.service.CityMapService;
import com.example.service.PathFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city-map")
public class CityMapController {

    @Autowired
    private CityMapService cityMapService;

    private CityMap loadedCityMap;

    /**
     * Endpoint pour charger une CityMap à partir d'un fichier XML.
     * @return La CityMap ou une erreur en cas de problème
     */
    @GetMapping("/loadmap")
    public ResponseEntity<?> getCityMap() {
        try {
            // Charger la CityMap à partir du fichier spécifié
            loadedCityMap = cityMapService.loadFromXML("src/main/resources/fichiersXMLPickupDelivery/petitPlan.xml");
            return ResponseEntity.ok("City map successfully loaded.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading city map from file: " + e.getMessage());
        }
    }

    /**
     * Endpoint pour calculer le chemin le plus rapide entre trois points.
     * @param startId   ID du point de départ
     * @param pickupId  ID du point de ramassage
     * @param dropoffId ID du point de dépôt
     * @return Le chemin calculé ou une erreur
     */
    @GetMapping("/fastest-path")
    public ResponseEntity<?> getFastestPath(
            @RequestParam("startId") long startId,
            @RequestParam("pickupId") long pickupId,
            @RequestParam("dropoffId") long dropoffId) {
        try {
            if (loadedCityMap == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("City map is not loaded. Please load the map first by calling /loadmap.");
            }

            // Calculer le chemin le plus rapide
            List<Long> fastestPath = PathFinder.findFastestPath(loadedCityMap, startId, pickupId, dropoffId);

            if (fastestPath == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No path found between the specified points.");
            }

            return ResponseEntity.ok(fastestPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating fastest path: " + e.getMessage());
        }
    }
    /*
    @GetMapping("/fastest-path-complete")
    public ResponseEntity<?> getFastestPathComplete(
            @RequestParam("startId") long startId,
            @RequestParam("pickupIds") List<Long> pickupIds,
            @RequestParam("dropoffIds") List<Long> dropoffIds) {
        try {
            if (loadedCityMap == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("City map is not loaded. Please load the map first by calling /loadmap.");
            }

            // Vérifiez que les listes pickupIds et dropoffIds ont la même taille
            if (pickupIds.size() != dropoffIds.size()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The lists of pickupIds and dropoffIds must have the same size.");
            }

            // Appeler la fonction findFastestPathComplete
            // List<Long> fastestPath = PathFinder.findFastestPathComplete(loadedCityMap, startId, pickupIds, dropoffIds);

            if (fastestPath == null || fastestPath.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No path found for the specified points.");
            }

            return ResponseEntity.ok(fastestPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating fastest path: " + e.getMessage());
        }
    }

     */

}
