package com.example.model;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@Entity // Indique que cette classe est une entité persistante
@XmlRootElement(name = "reseau")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reseau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Génération automatique de l'ID
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reseau") // Relation One-to-Many avec les noeuds
    @XmlElement(name = "noeud")
    private List<Noeud> noeuds;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reseau") // Relation One-to-Many avec les tronçons
    @XmlElement(name = "troncon")
    private List<Troncon> troncons;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

@Entity // Classe persistante
@XmlAccessorType(XmlAccessType.FIELD)
class Noeud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clé primaire avec auto-incrémentation
    private Long id;

    @XmlAttribute
    private double latitude;

    @XmlAttribute
    private double longitude;

    @ManyToOne // Chaque noeud est lié à un réseau
    @JoinColumn(name = "reseau_id") // Colonne FK pour le réseau
    private Reseau reseau;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

@Entity // Classe persistante
@XmlAccessorType(XmlAccessType.FIELD)
class Troncon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clé primaire avec auto-incrémentation
    private Long id;

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
