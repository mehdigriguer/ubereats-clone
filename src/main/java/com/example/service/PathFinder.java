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
        orderedPath.add(start);
        Set<Long> unvisitedPickups = new HashSet<>(pickups);
        Map<Long, Long> activeDeliveries = new HashMap<>(); // Map pickup -> corresponding dropoff
        Map<Long, Double> pickupTimes = new HashMap<>(); // Map pickup -> time it was picked up

        long current = start;
        double elapsedTime = 0.0; // Track the elapsed time in minutes

        while (!unvisitedPickups.isEmpty() || !activeDeliveries.isEmpty()) {
            System.out.println("Pickups: " + pickups);
            System.out.println("Dropoffs: " + dropoffs);
            System.out.println("Current Path: " + orderedPath);
            System.out.println("Elapsed Time: " + elapsedTime);
            System.out.println("activeDeliveries " + activeDeliveries);
            System.out.println("unvisitedPickups " + unvisitedPickups);
            System.out.println("current " + current);


            if (!unvisitedPickups.isEmpty()) {
                // Find the nearest pickup point
                long nearestPickup = findNearestPoint(cityMap, current, unvisitedPickups);
                if (nearestPickup != -1) {
                    unvisitedPickups.remove(nearestPickup);
                    activeDeliveries.put(nearestPickup, dropoffs.get(pickups.indexOf(nearestPickup)));


                    // Move to the nearest pickup
                    List<Long> pathToPickup = aStar(cityMap, current, nearestPickup);
                    orderedPath.addAll(pathToPickup.subList(1, pathToPickup.size())); // Skip current
                    elapsedTime += calculatePathTime(cityMap, pathToPickup);
                    current = nearestPickup;
                    pickupTimes.put(nearestPickup, elapsedTime);
                    continue;
                }
            }

            if (!activeDeliveries.isEmpty()) {
                // Find the nearest delivery that respects the 5-minute constraint
                long nearestDelivery = findFeasibleDeliveryWithConstraint(
                        cityMap, current, activeDeliveries, pickupTimes, elapsedTime);
                System.out.println("Feasible delivery found : " + nearestDelivery);

                if (nearestDelivery != -1) {
                    long correspondingPickup = getPickupForDelivery(pickups, dropoffs, nearestDelivery);
                    activeDeliveries.remove(correspondingPickup);

                    // Move to the delivery
                    List<Long> pathToDelivery = aStar(cityMap, current, nearestDelivery);
                    orderedPath.addAll(pathToDelivery.subList(1, pathToDelivery.size())); // Skip current
                    elapsedTime += calculatePathTime(cityMap, pathToDelivery);
                    current = nearestDelivery;
                    continue;
                }
            }

            throw new RuntimeException("No feasible route found.");
        }

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
            List<Long> path = aStar(cityMap, current, delivery);
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
            List<Long> path = aStar(cityMap, current, candidate);
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
            List<Long> toPickup = aStar(cityMap, currentLocation, pickup);
            if (toPickup == null) throw new RuntimeException("No path to pickup point: " + pickup);
            fullRoute.addAll(toPickup.subList(1, toPickup.size())); // Skip duplicate point

            // Path to delivery point
            List<Long> toDropoff = aStar(cityMap, pickup, dropoff);
            if (toDropoff == null) throw new RuntimeException("No path to dropoff point: " + dropoff);
            fullRoute.addAll(toDropoff.subList(1, toDropoff.size())); // Skip duplicate point

            // Update current location
            currentLocation = dropoff;
        }

        // Return to the warehouse
        List<Long> backToWarehouse = aStar(cityMap, currentLocation, warehouseId);
        if (backToWarehouse == null) throw new RuntimeException("No path back to warehouse.");
        fullRoute.addAll(backToWarehouse.subList(1, backToWarehouse.size())); // Skip duplicate point

        return fullRoute;
    }

    public static List<Long> findFastestPath(CityMap cityMap, long startId, long pickupId, long dropoffId) {
        List<Long> fullPath = new ArrayList<>();

        // Étape 1 : Trouver le chemin du point de départ au lieu de ramassage
        List<Long> toPickup = aStar(cityMap, startId, pickupId);
        if (toPickup == null) return null;

        // Étape 2 : Trouver le chemin du lieu de ramassage au lieu de dépôt
        List<Long> toDropoff = aStar(cityMap, pickupId, dropoffId);
        if (toDropoff == null) return null;

        // Combiner les chemins
        fullPath.addAll(toPickup);
        fullPath.addAll(toDropoff.subList(1, toDropoff.size())); // Exclure le premier nœud pour éviter les doublons

        return fullPath;
    }

    private static List<Long> aStar(CityMap cityMap, long start, long goal) {
        Map<Long, Double> gScore = new HashMap<>();
        Map<Long, Double> fScore = new HashMap<>();
        Map<Long, Long> cameFrom = new HashMap<>();

        PriorityQueue<long[]> openSet = new PriorityQueue<>(Comparator.comparingDouble(a -> fScore.getOrDefault(a[0], Double.MAX_VALUE)));
        gScore.put(start, 0.0);
        fScore.put(start, 0.0); // Estimation heuristique supprimée
        openSet.add(new long[]{start, 0});

        while (!openSet.isEmpty()) {
            long current = openSet.poll()[0];
            if (current == goal) return reconstructPath(cameFrom, current);
            for (RoadSegment segment : cityMap.getGraph().get(current)) {
                long neighbor = segment.getDestination();
                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + segment.getLength();
                double timeMinutes = tentativeGScore / (SPEED_KMH * 1000.0 / 60);
                if (timeMinutes > MAX_DELIVERY_TIME) continue;

                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore);
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
}
