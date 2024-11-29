package com.example.model;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@Entity // Classe persistante
@XmlAccessorType(XmlAccessType.FIELD)
public class Troncon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clé primaire avec auto-incrémentation
    private Integer id;

    @XmlAttribute
    private long origine;

    @XmlAttribute
    private long destination;

    @XmlAttribute(name = "nomRue")
    private String nomRue;

    @XmlAttribute(name = "longueur")
    private double longueur;

    @ManyToOne // Chaque tronçon est lié à un réseau
    @JoinColumn(name = "reseau_id") // Colonne FK pour le réseau
    private Reseau reseau;

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Reseau getReseau() {
        return reseau;
    }

    public void setReseau(Reseau reseau) {
        this.reseau = reseau;
    }
}
