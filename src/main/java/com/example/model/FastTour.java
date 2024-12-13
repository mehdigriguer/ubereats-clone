package com.example.model;

import java.util.List;

public class FastTour {
    private long startId;
    private long pickupId; // Unique, non une liste
    private long dropoffId; // Unique, non une liste

    // Getters et Setters
    public long getStartId() {
        return startId;
    }

    public void setStartId(long startId) {
        this.startId = startId;
    }

    public long getPickupId() {
        return pickupId;
    }

    public void setPickupId(long pickupId) {
        this.pickupId = pickupId;
    }

    public long getDropoffId() {
        return dropoffId;
    }

    public void setDropoffId(long dropoffId) {
        this.dropoffId = dropoffId;
    }
}


