package com.example.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityMap {
    private Map<Long, Intersection> intersections = new HashMap<>();
    private Map<Long, List<RoadSegment>> graph = new HashMap<>();

    public Map<Long, Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(Map<Long, Intersection> intersections) {
        this.intersections = intersections;
    }

    public Map<Long, List<RoadSegment>> getGraph() {
        return graph;
    }

    public void setGraph(Map<Long, List<RoadSegment>> graph) {
        this.graph = graph;
    }

    @Override
    public String toString() {
        return "CityMap{" +
                "intersections=" + intersections +
                ", graph=" + graph +
                '}';
    }
}