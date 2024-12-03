package com.example.model;

public class RoadSegment {
    private long origin; // Changed to long
    private long destination; // Changed to long
    private String streetName;
    private double length;

    public RoadSegment(long origin, long destination, String streetName, double length) {
        this.origin = origin;
        this.destination = destination;
        this.streetName = streetName;
        this.length = length;
    }

    public long getOrigin() {
        return origin;
    }

    public long getDestination() {
        return destination;
    }

    public String getStreetName() {
        return streetName;
    }

    public double getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "RoadSegment{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", streetName='" + streetName + '\'' +
                ", length=" + length +
                '}';
    }
}
