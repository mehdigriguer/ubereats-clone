//package com.example.controller;
//
//import com.example.model.CityMap;
//import com.example.model.FastTour;
//import com.example.service.CityMapService;
//import com.example.service.PathFinder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/city-map")
//public class CityMapController {
//
//    @Autowired
//    private CityMapService cityMapService;
//
//    private CityMap loadedCityMap;
//
//    /**
//     * Endpoint pour charger une CityMap à partir d'un fichier XML.
//     * @return La CityMap ou une erreur en cas de problème
//     */
//    @CrossOrigin(origins = "http://localhost:5173") // Allow requests from your frontend
//    @GetMapping("/loadmap")
//    public ResponseEntity<?> getCityMap() {
//        try {
//            // Load the city map from the XML file
//            CityMap loadedCityMap = cityMapService.loadFromXML("src/main/resources/fichiersXMLPickupDelivery/petitPlan.xml");
//
//            // Return the loaded city map as JSON
//            return ResponseEntity.ok(loadedCityMap);
//        } catch (Exception e) {
//            // Return an error message in case of failure
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error loading city map from file: " + e.getMessage());
//        }
//    }
//
//
//
//    /**
//     * Endpoint pour calculer le chemin le plus rapide entre trois points.
//     * @param startId   ID du point de départ
//     * @param pickupId  ID du point de ramassage
//     * @param dropoffId ID du point de dépôt
//     * @return Le chemin calculé ou une erreur
//     */
//    @PostMapping("/fastest-path")
//    public ResponseEntity<?> getFastestPath(@RequestBody FastTour deliveryRequest) {
//        try {
//            // Charger la carte si elle n'est pas encore chargée
//            if (loadedCityMap == null) {
//                System.out.println("Loading city map...");
//                loadedCityMap = cityMapService.loadFromXML("src/main/resources/fichiersXMLPickupDelivery/petitPlan.xml");
//            }
//
//            // Logs pour vérifier les données reçues
//            System.out.println("Start ID: " + deliveryRequest.getStartId());
//            System.out.println("Pickup ID: " + deliveryRequest.getPickupId());
//            System.out.println("Dropoff ID: " + deliveryRequest.getDropoffId());
//
//            // Validation des données
//            if (deliveryRequest.getStartId() <= 0 ||
//                    deliveryRequest.getPickupId() <= 0 ||
//                    deliveryRequest.getDropoffId() <= 0) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("Invalid request data.");
//            }
//
//            // Calculer le chemin pour des IDs uniques
//            List<Long> fastestPath = PathFinder.findFastestPath(
//                    loadedCityMap,
//                    deliveryRequest.getStartId(),
//                    deliveryRequest.getPickupId(),
//                    deliveryRequest.getDropoffId()
//            );
//
//            if (fastestPath == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No path found.");
//            }
//
//            return ResponseEntity.ok(fastestPath);
//        } catch (Exception e) {
//            System.err.println("Error occurred: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error calculating fastest path: " + e.getMessage());
//        }
//    }
//
//
//
//    /*
//    @GetMapping("/fastest-path-complete")
//    public ResponseEntity<?> getFastestPathComplete(
//            @RequestParam("startId") long startId,
//            @RequestParam("pickupIds") List<Long> pickupIds,
//            @RequestParam("dropoffIds") List<Long> dropoffIds) {
//        try {
//            if (loadedCityMap == null) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("City map is not loaded. Please load the map first by calling /loadmap.");
//            }
//
//            // Vérifiez que les listes pickupIds et dropoffIds ont la même taille
//            if (pickupIds.size() != dropoffIds.size()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("The lists of pickupIds and dropoffIds must have the same size.");
//            }
//
//            // Appeler la fonction findFastestPathComplete
//            // List<Long> fastestPath = PathFinder.findFastestPathComplete(loadedCityMap, startId, pickupIds, dropoffIds);
//
//            if (fastestPath == null || fastestPath.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body("No path found for the specified points.");
//            }
//
//            return ResponseEntity.ok(fastestPath);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error calculating fastest path: " + e.getMessage());
//        }
//    }
//
//     */
//
//}
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
    @CrossOrigin(origins = "http://localhost:5173") // Allow requests from your frontend
    @GetMapping("/loadmap")
    public ResponseEntity<?> getCityMap() {
        try {
            // Load the city map from the XML file
            CityMap loadedCityMap = cityMapService.loadFromXML("src/main/resources/fichiersXMLPickupDelivery/petitPlan.xml");

            // Return the loaded city map as JSON
            return ResponseEntity.ok(loadedCityMap);
        } catch (Exception e) {
            // Return an error message in case of failure
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
    @PostMapping("/fastest-path")
    public ResponseEntity<?> getFastestPath(@RequestBody FastTour deliveryRequest) {
        try {
            // Charger la carte si elle n'est pas encore chargée
            if (loadedCityMap == null) {
                System.out.println("Loading city map...");
                loadedCityMap = cityMapService.loadFromXML("src/main/resources/fichiersXMLPickupDelivery/petitPlan.xml");
            }

            // Logs pour vérifier les données reçues
            System.out.println("Start ID: " + deliveryRequest.getStartId());
            System.out.println("Pickup ID: " + deliveryRequest.getPickupId());
            System.out.println("Dropoff ID: " + deliveryRequest.getDropoffId());

            // Validation des données
            if (deliveryRequest.getStartId() <= 0 ||
                    deliveryRequest.getPickupId() <= 0 ||
                    deliveryRequest.getDropoffId() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid request data.");
            }

            // Calculer le chemin pour des IDs uniques
            List<Long> fastestPath = PathFinder.findFastestPath(
                    loadedCityMap,
                    deliveryRequest.getStartId(),
                    deliveryRequest.getPickupId(),
                    deliveryRequest.getDropoffId()
            );

            if (fastestPath == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No path found.");
            }

            return ResponseEntity.ok(fastestPath);
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
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
