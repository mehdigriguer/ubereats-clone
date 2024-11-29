package com.example.model;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "reseau")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reseau {
    @XmlElement(name = "noeud")
    private List<Noeud> noeuds;

    @XmlElement(name = "troncon")
    private List<Troncon> troncons;

    public List<Noeud> getNoeuds() {
        return noeuds;
    }

    public void setNoeuds(List<Noeud> noeuds) {
        this.noeuds = noeuds;
    }

    public List<Troncon> getTroncons() {
        return troncons;
    }

    public void setTroncons(List<Troncon> troncons) {
        this.troncons = troncons;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class Noeud {
    @XmlAttribute
    private long id;
    @XmlAttribute
    private double latitude;
    @XmlAttribute
    private double longitude;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}

@XmlAccessorType(XmlAccessType.FIELD)
class Troncon {
    @XmlAttribute
    private long origine;
    @XmlAttribute
    private long destination;
    @XmlAttribute(name = "nomRue")
    private String nomRue;
    @XmlAttribute(name = "longueur")
    private double longueur;

    // Getters and Setters
    public long getOrigine() {
        return origine;
    }

    public void setOrigine(long origine) {
        this.origine = origine;
    }

    public long getDestination() {
        return destination;
    }

    public void setDestination(long destination) {
        this.destination = destination;
    }

    public String getNomRue() {
        return nomRue;
    }

    public void setNomRue(String nomRue) {
        this.nomRue = nomRue;
    }

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }
}
