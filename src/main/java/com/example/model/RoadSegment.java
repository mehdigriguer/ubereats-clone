package com.example.model;

public class RoadSegment {
    private Intersection origin;
    private Intersection destination;
    private String name;
    private double length;

    // Constructors
    public RoadSegment() {}

    public RoadSegment(Intersection origin, Intersection destination, String name, double length) {
        this.origin = origin;
        this.destination = destination;
        this.name = name;
        this.length = length;
    }

    // Getters and Setters
    public Intersection getOrigin() {
        return origin;
    }

    public void setOrigin(Intersection origin) {
        this.origin = origin;
    }

    public Intersection getDestination() {
        return destination;
    }

    public void setDestination(Intersection destination) {
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}