package com.example.model;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@Entity // Classe persistante
@XmlAccessorType(XmlAccessType.FIELD)
public class Noeud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clé primaire avec auto-incrémentation
    private Integer id;

    @XmlAttribute
    private double latitude;

    @XmlAttribute
    private double longitude;

    @ManyToOne // Chaque noeud est lié à un réseau
    @JoinColumn(name = "reseau_id") // Colonne FK pour le réseau
    private Reseau reseau;

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Reseau getReseau() {
        return reseau;
    }

    public void setReseau(Reseau reseau) {
        this.reseau = reseau;
    }
}
