package com.example.service;

import com.example.model.CityMap;
import com.example.model.Intersection;
import com.example.model.RoadSegment;
import com.example.model.Reseau;
import com.example.model.Noeud;
import com.example.model.Troncon;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;

@Service
public class CityMapService {

    public CityMap loadFromXML(String filePath) throws Exception {
        try {
            // Charger le réseau à partir du fichier XML
            JAXBContext context = JAXBContext.newInstance(Reseau.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Reseau reseau = (Reseau) unmarshaller.unmarshal(new File(filePath));

            // Créer une nouvelle instance de CityMap
            CityMap cityMap = new CityMap();

            // Map intersections by their IDs
            HashMap<Integer, Intersection> intersectionMap = new HashMap<>();
            for (Noeud noeud : reseau.getNoeuds()) {
                Intersection intersection = new Intersection(
                        (int) noeud.getId(),
                        noeud.getLatitude(),
                        noeud.getLongitude()
                );
                cityMap.getIntersections().add(intersection);
                intersectionMap.put((int) noeud.getId(), intersection);
            }

            // Map road segments
            for (Troncon troncon : reseau.getTroncons()) {
                Intersection origin = intersectionMap.get((int) troncon.getOrigine());
                Intersection destination = intersectionMap.get((int) troncon.getDestination());
                RoadSegment roadSegment = new RoadSegment(
                        origin,
                        destination,
                        troncon.getNomRue(),
                        troncon.getLongueur()
                );
                cityMap.getRoadSegments().add(roadSegment);
            }

            return cityMap;
        } catch (JAXBException e) {
            throw new RuntimeException("Error parsing XML file", e);
        }
    }

}