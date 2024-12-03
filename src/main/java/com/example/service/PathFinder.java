package com.example.service;

import com.example.model.CityMap;
import com.example.model.RoadSegment;

import java.util.*;

public class PathFinder {
    private static final double MAX_DELIVERY_TIME = 5.0; // en minutes
    private static final double SPEED_KMH = 15.0; // vitesse du livreur en km/h

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
