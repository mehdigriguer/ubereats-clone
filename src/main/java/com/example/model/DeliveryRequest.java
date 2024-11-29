package com.example.model;

public class DeliveryRequest {
    private int id;
    private Intersection pickup;
    private Intersection delivery;
    private boolean isAssigned;

    // Constructors
    public DeliveryRequest() {}

    public DeliveryRequest(int id, Intersection pickup, Intersection delivery, boolean isAssigned) {
        this.id = id;
        this.pickup = pickup;
        this.delivery = delivery;
        this.isAssigned = isAssigned;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Intersection getPickup() {
        return pickup;
    }

    public void setPickup(Intersection pickup) {
        this.pickup = pickup;
    }

    public Intersection getDelivery() {
        return delivery;
    }

    public void setDelivery(Intersection delivery) {
        this.delivery = delivery;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
}