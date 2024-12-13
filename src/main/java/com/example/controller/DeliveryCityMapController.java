package com.example.controller;

import com.example.model.CityMap;
import com.example.model.FastTour;
import com.example.service.CityMapService;
import com.example.service.PathFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/city-map")
public class DeliveryCityMapController {

    @Autowired
    private CityMapService cityMapService;

    private CityMap loadedCityMap;

    /**
     * Endpoint pour charger une CityMap à partir d'un fichier XML.
     * @return La CityMap ou une erreur en cas de problème
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/loadmap")
    public ResponseEntity<?> getCityMap() {
        try {
            // Charger la carte depuis un fichier XML
            loadedCityMap = cityMapService.loadFromXML("src/main/resources/fichiersXMLPickupDelivery/petitPlan.xml");

            return ResponseEntity.ok(loadedCityMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading city map from file: " + e.getMessage());
        }
    }

    /**
     * Endpoint pour calculer le chemin le plus rapide entre trois points.
     * @param deliveryRequest Requête contenant startId, pickupId et dropoffId
     * @return Le chemin calculé ou une erreur
     */
    @PostMapping("/fastest-path")
    public ResponseEntity<?> getFastestPath(@RequestBody FastTour deliveryRequest) {
        try {
            if (loadedCityMap == null) {
                System.out.println("Loading city map...");
                loadedCityMap = cityMapService.loadFromXML("src/main/resources/fichiersXMLPickupDelivery/petitPlan.xml");
            }

            // Logs pour vérifier les données reçues
            System.out.println("Start ID: " + deliveryRequest.getStartId());
            System.out.println("Pickup ID: " + deliveryRequest.getPickupId());
            System.out.println("Dropoff ID: " + deliveryRequest.getDropoffId());

            if (deliveryRequest.getStartId() <= 0 ||
                    deliveryRequest.getPickupId() <= 0 ||
                    deliveryRequest.getDropoffId() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid request data.");
            }

            List<Long> fastestPath = PathFinder.findFastestPath(
                    loadedCityMap,
                    deliveryRequest.getStartId(),
                    deliveryRequest.getPickupId(),
                    deliveryRequest.getDropoffId()
            );

            if (fastestPath == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No path found.");
            }

            // Convertir les IDs en coordonnées
            List<double[]> coordinates = PathFinder.convertPathToCoordinates(cityMapService, fastestPath);

            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating fastest path: " + e.getMessage());
        }
    }

    /**
     * Endpoint pour créer une requête de livraison et calculer automatiquement le chemin.
     * @param request Requête contenant warehouseId, pickupId et deliveryId
     * @return Le chemin calculé ou une erreur
     */
    @PostMapping("/delivery/requests")
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Integer> request) {
        if (request == null || !request.containsKey("warehouseId") ||
                !request.containsKey("pickupId") || !request.containsKey("deliveryId")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: missing required fields.");
        }

        Integer warehouseId = request.get("warehouseId");
        Integer pickupId = request.get("pickupId");
        Integer deliveryId = request.get("deliveryId");

        if (warehouseId == null || pickupId == null || deliveryId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: IDs cannot be null.");
        }

        System.out.println("Received IDs: " + warehouseId + ", " + pickupId + ", " + deliveryId);

        // Créer l'objet FastTour
        FastTour fastTour = new FastTour();
        fastTour.setStartId(warehouseId);
        fastTour.setPickupId(pickupId);
        fastTour.setDropoffId(deliveryId);

        // Appeler la méthode getFastestPath et récupérer le résultat
        try {
            return getFastestPath(fastTour); // Appel direct à la méthode existante
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the request: " + e.getMessage());
        }
    }
}
