package com.example.model;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityMap {
    private List<Intersection> intersections = new ArrayList<>();
    private List<RoadSegment> roadSegments = new ArrayList<>();

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public List<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    // Load from XML
    public void loadFromXML(String filePath) {
        try {
            JAXBContext context = JAXBContext.newInstance(Reseau.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Reseau reseau = (Reseau) unmarshaller.unmarshal(new File(filePath));

            // Map intersections by their IDs
            HashMap<Integer, Intersection> intersectionMap = new HashMap<>();
            for (Noeud noeud : reseau.getNoeuds()) {
                Intersection intersection = new Intersection(
                        (int) noeud.getId(),
                        noeud.getLatitude(),
                        noeud.getLongitude()
                );
                intersections.add(intersection);
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
                roadSegments.add(roadSegment);
            }
        } catch (JAXBException e) {
            throw new RuntimeException("Error parsing XML file", e);
        }
    }
}
