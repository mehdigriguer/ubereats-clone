package com.example.utils;

import java.util.*;

public class State implements Cloneable {
    public long currentPosition;
    public List<Long> path;
    public Set<Long> unvisitedPickups;
    public Map<Long, Long> activeDeliveries;
    public Map<Long, Double> pickupTimes;
    public double elapsedTime;

    public State(long currentPosition, List<Long> path, Set<Long> unvisitedPickups, Map<Long, Long> activeDeliveries, double elapsedTime) {
        this.currentPosition = currentPosition;
        this.path = path;
        this.unvisitedPickups = unvisitedPickups;
        this.activeDeliveries = activeDeliveries;
        this.pickupTimes = new HashMap<>();
        this.elapsedTime = elapsedTime;
    }

    @Override
    public State clone() {
        try {
            State clone = (State) super.clone();
            clone.path = new ArrayList<>(this.path);
            clone.unvisitedPickups = new HashSet<>(this.unvisitedPickups);
            clone.activeDeliveries = new HashMap<>(this.activeDeliveries);
            clone.pickupTimes = new HashMap<>(this.pickupTimes);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning failed", e);
        }
    }
}