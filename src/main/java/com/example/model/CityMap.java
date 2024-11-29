package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class CityMap {
    private List<Intersection> intersections = new ArrayList<>();
    private List<RoadSegment> roadSegments = new ArrayList<>();

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public List<RoadSegment> getRoadSegments() {
        return roadSegments;
    }
}