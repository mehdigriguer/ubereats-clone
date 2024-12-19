package com.example.service;

import com.example.model.CityMap;
import com.example.model.RoadSegment;
import com.example.model.State;

import java.util.*;

public class PathFinder {
    private static final double MAX_DELIVERY_TIME = 5.0; // en minutes
    private static final double SPEED_KMH = 15.0; // vitesse du livreur en km/h


    public static List<Long> greedyOptimizeDeliverySequenceWithPath(
            CityMap cityMap,
            long start,
            List<Long> pickups,
            List<Long> dropoffs,
            List<Double> pickupDurations, // List of durations for each pickup in seconds
            List<Double> deliveryDurations // List of durations for each delivery in seconds
    ) {
        // Validate input sizes
        if (pickups.size() != pickupDurations.size() || dropoffs.size() != deliveryDurations.size()) {
            throw new IllegalArgumentException("Pickups and dropoffs must match the size of their respective durations.");
        }

        List<Long> bestPath = null;
        double bestTime = Double.MAX_VALUE;

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingDouble(state -> state.elapsedTime));
        queue.add(new State(start, new ArrayList<>(List.of(start)), new HashSet<>(pickups), new HashMap<>(), 0.0));

        while (!queue.isEmpty()) {
            State currentState = queue.poll();

            // Debugging: Current state
            System.out.println("\nExploring State:");
            System.out.println("Current Position: " + currentState.currentPosition);
            System.out.println("Path So Far: " + currentState.path);
            System.out.println("Elapsed Time: " + currentState.elapsedTime);
            System.out.println("Unvisited Pickups: " + currentState.unvisitedPickups);
            System.out.println("Active Deliveries: " + currentState.activeDeliveries);

            // Check if this state completes all deliveries
            if (currentState.unvisitedPickups.isEmpty() && currentState.activeDeliveries.isEmpty()) {
                // Add return to warehouse
                List<Long> pathToWarehouse = aStar(cityMap, currentState.currentPosition, start, false);
                if (pathToWarehouse != null) {
                    currentState.path.addAll(pathToWarehouse.subList(1, pathToWarehouse.size()));
                    currentState.elapsedTime += calculatePathTime(cityMap, pathToWarehouse);
                }

                System.out.println("Completed Path: " + currentState.path);
                System.out.println("Total Time: " + currentState.elapsedTime);

                if (currentState.elapsedTime < bestTime) {
                    bestTime = currentState.elapsedTime;
                    bestPath = new ArrayList<>(currentState.path);
                }
                continue;
            }

            // Explore pickups
            for (long pickup : currentState.unvisitedPickups) {
                List<Long> pathToPickup = aStar(cityMap, currentState.currentPosition, pickup, false);
                if (pathToPickup != null) {
                    System.out.println("Evaluating Pickup: " + pickup);
                    System.out.println("Path to Pickup: " + pathToPickup);

                    State newState = currentState.clone();
                    newState.path.addAll(pathToPickup.subList(1, pathToPickup.size()));
                    newState.elapsedTime += calculatePathTime(cityMap, pathToPickup);

                    // Convert pickup duration to minutes and add it
                    double pickupDurationInMinutes = pickupDurations.get(pickups.indexOf(pickup)) / 60.0;
                    newState.elapsedTime += pickupDurationInMinutes;
                    System.out.println("Pickup Duration Added (in minutes): " + pickupDurationInMinutes);

                    newState.currentPosition = pickup;

                    // Record pickup time immediately
                    newState.pickupTimes.put(pickup, newState.elapsedTime);
                    System.out.println("Pickup Time Recorded: " + pickup + " at " + newState.elapsedTime);

                    // Move pickup to active deliveries
                    newState.unvisitedPickups.remove(pickup);
                    newState.activeDeliveries.put(pickup, dropoffs.get(pickups.indexOf(pickup)));

                    queue.add(newState);
                }
            }

            // Explore deliveries
            for (Map.Entry<Long, Long> entry : currentState.activeDeliveries.entrySet()) {
                long pickup = entry.getKey();
                long delivery = entry.getValue();

                List<Long> pathToDelivery = aStar(cityMap, currentState.currentPosition, delivery, true);
                if (pathToDelivery != null) {
                    double timeToDelivery = currentState.elapsedTime + calculatePathTime(cityMap, pathToDelivery);

                    // Safeguard: Check if pickupTimes contains the pickup key
                    if (!currentState.pickupTimes.containsKey(pickup)) {
                        System.out.println("Warning: Pickup time not found for pickup " + pickup);
                        continue; // Skip this delivery if pickup time is missing
                    }

                    // Include durations from intermediate pickups/deliveries
                    double extraTime = calculateIntermediateDurations(
                            currentState.path,
                            pickup,
                            delivery,
                            pickupDurations,
                            deliveryDurations,
                            pickups,
                            dropoffs,
                            new HashSet<>(currentState.pickupTimes.keySet()), // Visited pickups
                            currentState.activeDeliveries // Active deliveries
                    );


                    // Convert delivery duration to minutes and add it
                    double deliveryDurationInMinutes = deliveryDurations.get(dropoffs.indexOf(delivery)) / 60.0;

                    // Total time from pickup to delivery
                    double timeFromPickup = timeToDelivery - currentState.pickupTimes.get(pickup) + extraTime;

                    System.out.println("Evaluating Delivery: " + delivery);
                    System.out.println("Path to Delivery: " + pathToDelivery);
                    System.out.println("Time to Delivery: " + timeToDelivery);
                    System.out.println("Extra Time from Intermediate Points: " + extraTime);
                    System.out.println("Delivery Duration Added (in minutes): " + deliveryDurationInMinutes);
                    System.out.println("Time from Pickup to Delivery: " + timeFromPickup);

                    if (timeFromPickup <= MAX_DELIVERY_TIME) {
                        State newState = currentState.clone();
                        newState.path.addAll(pathToDelivery.subList(1, pathToDelivery.size()));
                        newState.elapsedTime = timeToDelivery + deliveryDurationInMinutes;

                        newState.currentPosition = delivery;
                        newState.activeDeliveries.remove(pickup);
                        queue.add(newState);
                    }
                }
            }
        }

        if (bestPath == null) {
            throw new RuntimeException("No feasible route found.");
        }

        System.out.println("\nBest Path Found: " + bestPath);
        System.out.println("Minimum Time: " + bestTime);
        return bestPath;
    }

    public static List<Long> greedyOptimizeDeliverySequenceWithPath(
            CityMap cityMap,
            long start,
            List<Long> pickups,
            List<Long> dropoffs) {
        return greedyOptimizeDeliverySequenceWithPath(cityMap, start, pickups, dropoffs,
                Collections.nCopies(pickups.size(), 0.0), // Default pickup durations
                Collections.nCopies(dropoffs.size(), 0.0) // Default delivery durations
        );
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

    private static double calculateIntermediateDurations(
            List<Long> path,
            long pickup,
            long delivery,
            List<Double> pickupDurations,
            List<Double> deliveryDurations,
            List<Long> pickups,
            List<Long> dropoffs,
            Set<Long> visitedPickups,
            Map<Long, Long> activeDeliveries) {

        double extraTime = 0.0;

        // Iterate through the current path
        for (long point : path) {
            // Skip the pickup and delivery points themselves
            if (point == pickup || point == delivery) continue;

            // Check if the point is a visited pickup
            if (visitedPickups.contains(point)) {
                int index = pickups.indexOf(point);
                if (index != -1) {
                    extraTime += pickupDurations.get(index) / 60.0; // Convert to minutes
                    System.out.println("Adding Pickup Duration at Point: " + point + " (" + (pickupDurations.get(index) / 60.0) + " minutes)");
                }
            }

            // Check if the point is an active delivery
            if (activeDeliveries.containsValue(point)) {
                int index = dropoffs.indexOf(point);
                if (index != -1) {
                    extraTime += deliveryDurations.get(index) / 60.0; // Convert to minutes
                    System.out.println("Adding Delivery Duration at Point: " + point + " (" + (deliveryDurations.get(index) / 60.0) + " minutes)");
                }
            }
        }

        return extraTime;
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
