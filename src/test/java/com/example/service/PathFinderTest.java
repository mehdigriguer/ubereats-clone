package com.example.service;

import com.example.model.CityMap;
import com.example.model.Intersection;
import com.example.model.RoadSegment;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PathFinderTest {

    @Test
    void testOptimizeForDeliveryTimes() {
        CityMap cityMap = new CityMap();

        Map<Long, Intersection> intersections = new HashMap<>();
        for (long i = 1; i <= 5; i++) {
            intersections.put(i, new Intersection(i, 45.0 + i, 5.0 + i));
        }
        cityMap.setIntersections(intersections);

        Map<Long, List<RoadSegment>> graph = new HashMap<>();
        graph.put(1L, Arrays.asList(new RoadSegment(1L, 2L, "Street A", 1000)));
        graph.put(2L, Arrays.asList(new RoadSegment(2L, 4L, "Street B", 100)));
        graph.put(4L, Arrays.asList(new RoadSegment(4L, 3L, "Street C", 100)));
        graph.put(3L, Arrays.asList(new RoadSegment(3L, 5L, "Street D", 100)));
        graph.put(5L, Arrays.asList(new RoadSegment(5L, 1L, "Street E", 2000)));
        cityMap.setGraph(graph);

        long start = 1;
        List<Long> pickups = Arrays.asList(2L, 4L);
        List<Long> dropoffs = Arrays.asList(3L, 5L);

        List<Long> result = PathFinder.greedyOptimizeDeliverySequenceWithPath(cityMap, start, pickups, dropoffs);

        assertNotNull(result, "The result should not be null");
        System.out.println("Optimized Path: " + result);

        List<Long> expectedPath = Arrays.asList(1L, 2L, 4L, 3L, 5L, 1L);
        assertEquals(expectedPath, result, "The optimized path is not as expected");
    }


    @Test
    void testConstrainedPathReordering() {
        CityMap cityMap = new CityMap();

        // Add intersections
        Map<Long, Intersection> intersections = new HashMap<>();
        for (long i = 1; i <= 5; i++) {
            intersections.put(i, new Intersection(i, 45.0 + i, 5.0 + i));
        }
        cityMap.setIntersections(intersections);

        // Add road segments
        Map<Long, List<RoadSegment>> graph = new HashMap<>();
        graph.put(1L, Arrays.asList(new RoadSegment(1L, 2L, "Street A", 2000),
                                    new RoadSegment(1L, 4L, "Street B", 1000)));
        graph.put(2L, Arrays.asList(new RoadSegment(2L, 3L, "Street C", 100),
                                    new RoadSegment(2L, 4L, "Street D", 100)));
        graph.put(3L, Arrays.asList(new RoadSegment(3L, 5L, "Street E", 100),
                                    new RoadSegment(3L, 4L, "Street F", 500)));
        graph.put(4L, Arrays.asList(new RoadSegment(4L, 5L, "Street G", 600),
                                    new RoadSegment(4L, 2L, "Street H", 100),
                                    new RoadSegment(4L, 3L, "Street I", 500)));
        graph.put(5L, Arrays.asList(new RoadSegment(5L, 3L, "Street J", 100),
                                    new RoadSegment(5L, 1L, "Street K", 4000)));
        cityMap.setGraph(graph);

        // Define the test scenario
        long start = 1;
        List<Long> pickups = Arrays.asList(2L, 4L);
        List<Long> dropoffs = Arrays.asList(3L, 5L);

        // Run the pathfinding method
        List<Long> result = PathFinder.greedyOptimizeDeliverySequenceWithPath(cityMap, start, pickups, dropoffs);

        // Validate results
        assertNotNull(result, "The result should not be null");
        System.out.println("Optimized Path: " + result);

        // Expected path: Reordering due to constraints
        List<Long> expectedPath = Arrays.asList(1L, 4L, 2L, 3L, 5L, 1L);
        assertEquals(expectedPath, result, "The optimized path is not as expected");
    }

    @Test
    void testConstrainedPathReorderingWithDurations() {
        CityMap cityMap = new CityMap();

        // Add intersections
        Map<Long, Intersection> intersections = new HashMap<>();
        for (long i = 1; i <= 5; i++) {
            intersections.put(i, new Intersection(i, 45.0 + i, 5.0 + i));
        }
        cityMap.setIntersections(intersections);

        // Add road segments
        Map<Long, List<RoadSegment>> graph = new HashMap<>();
        graph.put(1L, Arrays.asList(new RoadSegment(1L, 2L, "Street A", 2000),
                                    new RoadSegment(1L, 4L, "Street B", 1000)));
        graph.put(2L, Arrays.asList(new RoadSegment(2L, 3L, "Street C", 100),
                                    new RoadSegment(2L, 4L, "Street D", 100)));
        graph.put(3L, Arrays.asList(new RoadSegment(3L, 5L, "Street E", 100),
                                    new RoadSegment(3L, 4L, "Street F", 500)));
        graph.put(4L, Arrays.asList(new RoadSegment(4L, 5L, "Street G", 600),
                                    new RoadSegment(4L, 2L, "Street H", 1000),
                                    new RoadSegment(4L, 3L, "Street I", 500)));
        graph.put(5L, Arrays.asList(new RoadSegment(5L, 3L, "Street J", 100),
                                    new RoadSegment(5L, 1L, "Street K", 4000)));
        cityMap.setGraph(graph);

        // Define the test scenario
        long start = 1;
        List<Long> pickups = Arrays.asList(2L, 4L);
        List<Long> dropoffs = Arrays.asList(3L, 5L);

        // Define pickup and delivery durations (in seconds)
        List<Double> pickupDurations = Arrays.asList(120.0, 30.0);  // 2 minutes and 3 minutes
        List<Double> deliveryDurations = Arrays.asList(30.0, 300.0);  // 4 minutes and 5 minutes

        // Run the pathfinding method
        List<Long> result = PathFinder.greedyOptimizeDeliverySequenceWithPath(
                cityMap, start, pickups, dropoffs, pickupDurations, deliveryDurations);

        // Validate results
        assertNotNull(result, "The result should not be null");
        System.out.println("Optimized Path: " + result);

        // Expected path: Reordering due to constraints
        List<Long> expectedPath = Arrays.asList(1L, 2L, 4L, 3L, 5L, 1L);
        assertEquals(expectedPath, result, "The optimized path is not as expected");
    }



    @Test
    void testManyPickupsBeforeDeliveries() {
        // Create a mock CityMap
        CityMap cityMap = new CityMap();

        // Add intersections
        Map<Long, Intersection> intersections = new HashMap<>();
        intersections.put(1L, new Intersection(1L, 45.0, 5.0));
        intersections.put(2L, new Intersection(2L, 45.1, 5.1));
        intersections.put(3L, new Intersection(3L, 45.2, 5.2));
        intersections.put(4L, new Intersection(4L, 45.3, 5.3));
        intersections.put(5L, new Intersection(5L, 45.4, 5.4));
        intersections.put(6L, new Intersection(6L, 45.5, 5.5));
        intersections.put(7L, new Intersection(7L, 45.6, 5.6));
        cityMap.setIntersections(intersections);

        // Add road segments
        Map<Long, List<RoadSegment>> graph = new HashMap<>();
        graph.put(1L, Arrays.asList(new RoadSegment(1L, 2L, "Street A", 1.0)));
        graph.put(2L, Arrays.asList(new RoadSegment(2L, 3L, "Street B", 1.5)));
        graph.put(3L, Arrays.asList(new RoadSegment(3L, 4L, "Street C", 2.0)));
        graph.put(4L, Arrays.asList(new RoadSegment(4L, 5L, "Street D", 1.2)));
        graph.put(5L, Arrays.asList(new RoadSegment(5L, 6L, "Street E", 2.5)));
        graph.put(6L, Arrays.asList(new RoadSegment(6L, 7L, "Street F", 1.5)));
        cityMap.setGraph(graph);

        // Define the test scenario
        long start = 1;
        List<Long> pickups = Arrays.asList(2L, 3L, 4L);
        List<Long> dropoffs = Arrays.asList(5L, 6L, 7L);

        // Run the pathfinding method
        List<Long> result = PathFinder.greedyOptimizeDeliverySequenceWithPath(cityMap, start, pickups, dropoffs);

        // Validate results
        List<Long> expectedPath = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L);
        assertEquals(expectedPath, result, "The optimized path is not as expected");
    }

    @Test
    void testRevisitSameNode() {
        // Create a mock CityMap
        CityMap cityMap = new CityMap();

        // Add intersections
        Map<Long, Intersection> intersections = new HashMap<>();
        intersections.put(1L, new Intersection(1L, 45.0, 5.0));
        intersections.put(2L, new Intersection(2L, 45.1, 5.1));
        intersections.put(3L, new Intersection(3L, 45.2, 5.2));
        intersections.put(4L, new Intersection(4L, 45.3, 5.3));
        cityMap.setIntersections(intersections);

        // Add road segments
        Map<Long, List<RoadSegment>> graph = new HashMap<>();
        graph.put(1L, Arrays.asList(new RoadSegment(1L, 2L, "Street A", 1.0)));
        graph.put(2L, Arrays.asList(new RoadSegment(2L, 3L, "Street B", 1.5),
                new RoadSegment(2L, 4L, "Street C", 2.0)));
        graph.put(4L, Arrays.asList(new RoadSegment(4L, 2L, "Street D", 1.2)));
        cityMap.setGraph(graph);

        // Define the test scenario
        long start = 1;
        List<Long> pickups = Arrays.asList(2L, 4L);
        List<Long> dropoffs = Arrays.asList(3L, 2L);

        // Run the pathfinding method
        List<Long> result = PathFinder.greedyOptimizeDeliverySequenceWithPath(cityMap, start, pickups, dropoffs);

        // Validate results
        List<Long> expectedPath = Arrays.asList(1L, 2L, 3L, 4L, 2L);
        assertEquals(expectedPath, result, "The optimized path is not as expected");
    }

    @Test
    void testUnreachableDropoffDueToConstraints() {
        // Create a mock CityMap
        CityMap cityMap = new CityMap();

        // Add intersections
        Map<Long, Intersection> intersections = new HashMap<>();
        intersections.put(1L, new Intersection(1L, 45.0, 5.0));
        intersections.put(2L, new Intersection(2L, 45.1, 5.1));
        intersections.put(3L, new Intersection(3L, 45.2, 5.2));
        intersections.put(4L, new Intersection(4L, 45.3, 5.3));
        intersections.put(5L, new Intersection(5L, 45.4, 5.4));
        cityMap.setIntersections(intersections);

        // Add road segments
        Map<Long, List<RoadSegment>> graph = new HashMap<>();
        graph.put(1L, Arrays.asList(new RoadSegment(1L, 2L, "Street A", 1.0)));
        graph.put(2L, Arrays.asList(new RoadSegment(2L, 3L, "Street B", 3.5))); // Too long for the constraint
        graph.put(3L, Arrays.asList(new RoadSegment(3L, 4L, "Street C", 2.0)));
        graph.put(4L, Arrays.asList(new RoadSegment(4L, 5L, "Street D", 1.2)));
        cityMap.setGraph(graph);

        // Define the test scenario
        long start = 1;
        List<Long> pickups = Arrays.asList(2L, 4L);
        List<Long> dropoffs = Arrays.asList(5L, 3L);

        // Run the pathfinding method and expect an exception
        Exception exception = assertThrows(RuntimeException.class, () ->
                PathFinder.greedyOptimizeDeliverySequenceWithPath(cityMap, start, pickups, dropoffs));

        assertTrue(exception.getMessage().contains("No feasible route found"), "Expected exception for unreachable dropoff");
    }


}
