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
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reseau") // Relation One-to-Many avec les noeuds
    @XmlElement(name = "noeud")
    private List<Noeud> noeuds;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reseau") // Relation One-to-Many avec les tronçons
    @XmlElement(name = "troncon")
    private List<Troncon> troncons;

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

