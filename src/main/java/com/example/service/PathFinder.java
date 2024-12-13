package com.example.service;

import com.example.model.CityMap;
import com.example.model.RoadSegment;

import java.util.*;

public class PathFinder {
    private static final double MAX_DELIVERY_TIME = 5.0; // en minutes
    private static final double SPEED_KMH = 15.0; // vitesse du livreur en km/h

    public static List<Long> optimizeDeliverySequenceWithPath(
            CityMap cityMap, long start, List<Long> pickups, List<Long> dropoffs) {

        List<Long> orderedPath = new ArrayList<>();
        orderedPath.add(start); // Add start point
        Set<Long> unvisitedPickups = new HashSet<>(pickups);
        Map<Long, Long> activeDeliveries = new HashMap<>(); // Map pickup -> corresponding dropoff
        Map<Long, Double> pickupTimes = new HashMap<>(); // Map pickup -> time it was picked up
        System.out.println("List of pickups: " + pickups);
        System.out.println("List of dropoffs: " + dropoffs);


        long current = start;
        double elapsedTime = 0.0; // Track the elapsed time in minutes

        // Move to the first pickup (warehouse-to-first-pickup path is not optimized)
        if (!unvisitedPickups.isEmpty()) {
            long firstPickup = findNearestPoint(cityMap, current, unvisitedPickups);
            List<Long> pathToFirstPickup = aStar(cityMap, current, firstPickup, false);
            System.out.println("Moving to first pickup: " + firstPickup);
            System.out.println("Path to first pickup: " + pathToFirstPickup);
            orderedPath.addAll(pathToFirstPickup.subList(1, pathToFirstPickup.size()));
            elapsedTime += calculatePathTime(cityMap, pathToFirstPickup);
            current = firstPickup;
            unvisitedPickups.remove(firstPickup);
            activeDeliveries.put(firstPickup, dropoffs.get(pickups.indexOf(firstPickup)));
            pickupTimes.put(firstPickup, elapsedTime);
        }

        // Optimize between all pickups and deliveries
        while (!unvisitedPickups.isEmpty() || !activeDeliveries.isEmpty()) {
            System.out.println("\nCurrent State:");
            System.out.println("Current Position: " + current);
            System.out.println("Elapsed Time: " + elapsedTime);
            System.out.println("Ordered Path: " + orderedPath);
            System.out.println("Unvisited Pickups: " + unvisitedPickups);
            System.out.println("Active Deliveries: " + activeDeliveries);

            double minTime = Double.MAX_VALUE;
            long nextPoint = -1;
            boolean isPickup = false;

            // Evaluate unvisited pickups
            for (long pickup : unvisitedPickups) {
                List<Long> pathToPickup = aStar(cityMap, current, pickup, true);
                if (pathToPickup != null) {
                    double timeToPickup = elapsedTime + calculatePathTime(cityMap, pathToPickup);
                    System.out.println("Evaluating pickup: " + pickup);
                    System.out.println("Path to pickup: " + pathToPickup);
                    System.out.println("Time to pickup: " + timeToPickup);

                    if (timeToPickup < minTime) {
                        minTime = timeToPickup;
                        nextPoint = pickup;
                        isPickup = true;
                    }
                }
            }

            // Evaluate active deliveries
            for (Map.Entry<Long, Long> entry : activeDeliveries.entrySet()) {
                long pickup = entry.getKey();
                long delivery = entry.getValue();

                List<Long> pathToDelivery = aStar(cityMap, current, delivery, true);
                if (pathToDelivery != null) {
                    double timeToDelivery = elapsedTime + calculatePathTime(cityMap, pathToDelivery);
                    System.out.println("Evaluating delivery: " + delivery);
                    System.out.println("Path to delivery: " + pathToDelivery);
                    System.out.println("Time to delivery: " + timeToDelivery);
                    System.out.println("Time from pickup to delivery: " + (timeToDelivery - pickupTimes.get(pickup)));

                    if (timeToDelivery - pickupTimes.get(pickup) <= MAX_DELIVERY_TIME && timeToDelivery < minTime) {
                        minTime = timeToDelivery;
                        nextPoint = delivery;
                        isPickup = false;
                    }
                }
            }

            if (nextPoint == -1) {
                throw new RuntimeException("No feasible route found.");
            }

            // Move to the next point
            List<Long> pathToNext = aStar(cityMap, current, nextPoint, true);
            System.out.println("\nMoving to next point: " + nextPoint);
            System.out.println("Path to next point: " + pathToNext);
            orderedPath.addAll(pathToNext.subList(1, pathToNext.size()));
            elapsedTime += calculatePathTime(cityMap, pathToNext);
            current = nextPoint;

            if (isPickup) {
                unvisitedPickups.remove(nextPoint);
                activeDeliveries.put(nextPoint, dropoffs.get(pickups.indexOf(nextPoint)));
                pickupTimes.put(nextPoint, elapsedTime);
                System.out.println("Pickup completed: " + nextPoint);
                System.out.println("Active Deliveries Updated: " + activeDeliveries);
            } else {
                long correspondingPickup = getPickupForDelivery(pickups, dropoffs, nextPoint);
                activeDeliveries.remove(correspondingPickup);
                System.out.println("Delivery completed: " + nextPoint);
                System.out.println("Active Deliveries Updated: " + activeDeliveries);
            }
        }

        // Add the return path to the warehouse (optional, not part of optimization)
        List<Long> pathToWarehouse = aStar(cityMap, current, start, false);
        if (pathToWarehouse != null) {
            System.out.println("\nReturning to warehouse...");
            System.out.println("Path to warehouse: " + pathToWarehouse);
            orderedPath.addAll(pathToWarehouse.subList(1, pathToWarehouse.size()));
        }

        System.out.println("\nFinal Optimized Path: " + orderedPath);
        return orderedPath;
    }



    private static long findFeasibleDeliveryWithConstraint(
            CityMap cityMap, long current, Map<Long, Long> activeDeliveries,
            Map<Long, Double> pickupTimes, double elapsedTime) {

        long nearest = -1;
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<Long, Long> entry : activeDeliveries.entrySet()) {
            long pickup = entry.getKey();
            long delivery = entry.getValue();

            // Path from current to delivery
            List<Long> path = aStar(cityMap, current, delivery, true);
            System.out.println("Pickup: " + pickup);
            System.out.println("Delivery: " + delivery);
            System.out.println("Path to Delivery: " + path);
            if (path != null) {
                double distance = calculatePathDistance(cityMap, path);
                double timeToDelivery = elapsedTime + (distance / (SPEED_KMH * 1000.0 / 60));
                System.out.println("Checking delivery from pickup: " + pickup + " to delivery: " + delivery);
                System.out.println("Pickup Times: " + pickupTimes);
                System.out.println("Elapsed Time: " + elapsedTime);
                System.out.println("Path to Delivery: " + path);
                System.out.println("Time to Delivery: " + timeToDelivery);
                System.out.println("Time between pickup and delivery: " + (timeToDelivery - pickupTimes.get(pickup)));

                // Check if the time between pickup and delivery respects the 5-minute constraint
                if (timeToDelivery - pickupTimes.get(pickup) <= MAX_DELIVERY_TIME && distance < minDistance) {
                    minDistance = distance;
                    nearest = delivery;
                }

            }
        }

        return nearest;
    }

    private static long findNearestPoint(CityMap cityMap, long current, Set<Long> candidates) {
        long nearest = -1;
        double minDistance = Double.MAX_VALUE;

        for (long candidate : candidates) {
            List<Long> path = aStar(cityMap, current, candidate, true);
            if (path != null) {
                double distance = calculatePathDistance(cityMap, path);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = candidate;
                }
            }
        }

        return nearest;
    }

    private static double calculatePathDistance(CityMap cityMap, List<Long> path) {
        double distance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            long from = path.get(i);
            long to = path.get(i + 1);
            for (RoadSegment segment : cityMap.getGraph().get(from)) {
                if (segment.getDestination() == to) {
                    distance += segment.getLength();
                    break;
                }
            }
        }
        return distance;
    }

    private static double calculatePathTime(CityMap cityMap, List<Long> path) {
        double distance = calculatePathDistance(cityMap, path); // Ensure calculatePathDistance is implemented
        return distance / (SPEED_KMH * 1000.0 / 60); // Convert km/h to minutes
    }

    private static long getPickupForDelivery(List<Long> pickups, List<Long> dropoffs, long delivery) {
        for (int i = 0; i < dropoffs.size(); i++) {
            if (dropoffs.get(i).equals(delivery)) {
                return pickups.get(i);
            }
        }
        throw new IllegalArgumentException("No matching pickup found for delivery: " + delivery);
    }


    public static List<Long> planDeliveryRoute(CityMap cityMap, long warehouseId, List<Long> pickupPoints, List<Long> dropoffPoints) {
        if (pickupPoints.size() != dropoffPoints.size()) {
            throw new IllegalArgumentException("Pickup and dropoff points must match in count.");
        }

        List<Long> fullRoute = new ArrayList<>();
        long currentLocation = warehouseId;

        // Add warehouse as the starting point
        fullRoute.add(warehouseId);

        for (int i = 0; i < pickupPoints.size(); i++) {
            long pickup = pickupPoints.get(i);
            long dropoff = dropoffPoints.get(i);

            // Path to pickup point
            List<Long> toPickup = aStar(cityMap, currentLocation, pickup, true);
            if (toPickup == null) throw new RuntimeException("No path to pickup point: " + pickup);
            fullRoute.addAll(toPickup.subList(1, toPickup.size())); // Skip duplicate point

            // Path to delivery point
            List<Long> toDropoff = aStar(cityMap, pickup, dropoff, true);
            if (toDropoff == null) throw new RuntimeException("No path to dropoff point: " + dropoff);
            fullRoute.addAll(toDropoff.subList(1, toDropoff.size())); // Skip duplicate point

            // Update current location
            currentLocation = dropoff;
        }

        // Return to the warehouse
        List<Long> backToWarehouse = aStar(cityMap, currentLocation, warehouseId, false);
        if (backToWarehouse == null) throw new RuntimeException("No path back to warehouse.");
        fullRoute.addAll(backToWarehouse.subList(1, backToWarehouse.size())); // Skip duplicate point

        return fullRoute;
    }

    public static List<Long> findFastestPath(CityMap cityMap, long startId, long pickupId, long dropoffId) {
        List<Long> fullPath = new ArrayList<>();

        // Étape 1 : Trouver le chemin du point de départ au lieu de ramassage
        List<Long> toPickup = aStar(cityMap, startId, pickupId, false);
        if (toPickup == null) return null;

        // Étape 2 : Trouver le chemin du lieu de ramassage au lieu de dépôt
        List<Long> toDropoff = aStar(cityMap, pickupId, dropoffId, true);
        if (toDropoff == null) return null;

        // Combiner les chemins
        fullPath.addAll(toPickup);
        fullPath.addAll(toDropoff.subList(1, toDropoff.size())); // Exclure le premier nœud pour éviter les doublons

        return fullPath;
    }

    private static List<Long> aStar(CityMap cityMap, long start, long goal, boolean applyConstraint) {
        Map<Long, Double> gScore = new HashMap<>();
        Map<Long, Double> fScore = new HashMap<>();
        Map<Long, Long> cameFrom = new HashMap<>();

        PriorityQueue<long[]> openSet = new PriorityQueue<>(Comparator.comparingDouble(a -> fScore.getOrDefault(a[0], Double.MAX_VALUE)));
        gScore.put(start, 0.0);
        fScore.put(start, 0.0); // Heuristic removed for simplicity
        openSet.add(new long[]{start, 0});

        while (!openSet.isEmpty()) {
            long current = openSet.poll()[0];
            if (current == goal) return reconstructPath(cameFrom, current);

            for (RoadSegment segment : cityMap.getGraph().get(current)) {
                long neighbor = segment.getDestination();
                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + segment.getLength();
                double timeMinutes = tentativeGScore / (SPEED_KMH * 1000.0 / 60);

                // Skip neighbors violating the 5-minute constraint, if enabled
                if (applyConstraint && timeMinutes > MAX_DELIVERY_TIME) continue;

                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore); // Only considering gScore for simplicity
                    openSet.add(new long[]{neighbor, (long) tentativeGScore});
                }
            }
        }
        return null;
    }

    private static List<Long> reconstructPath(Map<Long, Long> cameFrom, long current) {
        List<Long> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) { // Continue tant que le chemin remonte
            path.add(0, current);
            current = cameFrom.get(current);
        }
        path.add(0, current); // Ajouter le premier nœud (point de départ)
        return path;
    }

    public static List<double[]> convertPathToCoordinates(CityMapService cityMapService, List<Long> path) {
        List<double[]> coordinates = new ArrayList<>();
        for (Long id : path) {
            coordinates.add(cityMapService.findLatLongFromId(id));
        }
        return coordinates;
    }

}
