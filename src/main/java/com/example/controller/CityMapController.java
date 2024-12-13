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
    /**
     * Endpoint to optimize the delivery sequence.
     * @param startId   ID of the warehouse or start point
     * @param pickups   List of pickup point IDs
     * @param dropoffs  List of dropoff point IDs
     * @return The optimized path or an error message
     */
    @PostMapping("/optimize-delivery-sequence")
    public ResponseEntity<?> optimizeDeliverySequence(
            @RequestParam("startId") long startId,
            @RequestBody List<Long> pickups,
            @RequestBody List<Long> dropoffs) {
        try {
            if (loadedCityMap == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("City map is not loaded. Please load the map first by calling /loadmap.");
            }

            if (pickups.size() != dropoffs.size()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The number of pickups and dropoffs must match.");
            }

            List<Long> optimizedPath = PathFinder.optimizeDeliverySequenceWithPath(loadedCityMap, startId, pickups, dropoffs);

            return ResponseEntity.ok(optimizedPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error optimizing delivery sequence: " + e.getMessage());
        }
    }

    /**
     * Endpoint to plan a predefined delivery route.
     * @param startId   ID of the warehouse or start point
     * @param pickups   List of pickup point IDs
     * @param dropoffs  List of dropoff point IDs
     * @return The planned route or an error message
     */
    @PostMapping("/plan-delivery-route")
    public ResponseEntity<?> planDeliveryRoute(
            @RequestParam("startId") long startId,
            @RequestBody List<Long> pickups,
            @RequestBody List<Long> dropoffs) {
        try {
            if (loadedCityMap == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("City map is not loaded. Please load the map first by calling /loadmap.");
            }

            if (pickups.size() != dropoffs.size()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The number of pickups and dropoffs must match.");
            }

            List<Long> plannedRoute = PathFinder.planDeliveryRoute(loadedCityMap, startId, pickups, dropoffs);

            return ResponseEntity.ok(plannedRoute);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error planning delivery route: " + e.getMessage());
        }
    }
}
