package service;

import com.example.model.CityMap;
import com.example.model.Intersection;
import com.example.model.RoadSegment;
import com.example.service.PathFinder;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PathFinderTest {

    @Test
    void testConstrainedPathReorderingWithDurations() {
        CityMap cityMap = new CityMap();

        // Add intersections
        List<Intersection> intersections = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            intersections.add(new Intersection(i, 45.0 + i, 5.0 + i));
        }
        cityMap.setIntersections(intersections);

        // Add road segments
        List<RoadSegment> roadSegments = new ArrayList<>();
        roadSegments.add(new RoadSegment(1L, 2L, "Street A", 2000));
        roadSegments.add(new RoadSegment(1L, 4L, "Street B", 1000));
        roadSegments.add(new RoadSegment(2L, 3L, "Street C", 100));
        roadSegments.add(new RoadSegment(2L, 4L, "Street D", 100));
        roadSegments.add(new RoadSegment(3L, 5L, "Street E", 100));
        roadSegments.add(new RoadSegment(3L, 4L, "Street F", 500));
        roadSegments.add(new RoadSegment(4L, 5L, "Street G", 600));
        roadSegments.add(new RoadSegment(4L, 2L, "Street H", 1000));
        roadSegments.add(new RoadSegment(4L, 3L, "Street I", 500));
        roadSegments.add(new RoadSegment(5L, 3L, "Street J", 100));
        roadSegments.add(new RoadSegment(5L, 1L, "Street K", 4000));
        cityMap.setRoadSegments(roadSegments);

        // Define the test scenario
        long start = 1;
        List<Long> pickups = Arrays.asList(2L, 4L);
        List<Long> dropoffs = Arrays.asList(3L, 5L);

        // Define pickup and delivery durations (in seconds)
        List<Double> pickupDurations = Arrays.asList(120.0, 30.0);  // 2 minutes and 0.5 minutes
        List<Double> deliveryDurations = Arrays.asList(40.0, 300.0);  // 0.5 minutes and 5 minutes

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


}