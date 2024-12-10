package com.example.model;

public class FastTour {
    private long startId;
    private long pickupId;
    private long dropoffId;

    // Getters et setters
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
