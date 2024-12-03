package com.example.service;

import com.example.model.CityMap;
import com.example.model.Intersection;
import com.example.model.RoadSegment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

@Service
public class CityMapService {

    private final List<CityMap> cityMaps = new ArrayList<>();

    // Load a CityMap from an XML file
    public CityMap loadFromXML(String filePath) {
        CityMap cityMap = new CityMap();

        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Parse intersections (noeuds)
            NodeList nodeList = doc.getElementsByTagName("noeud");
            Map<Long, Intersection> intersections = new HashMap<>();
            Map<Long, List<RoadSegment>> graph = new HashMap<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element nodeElement = (Element) nodeList.item(i);
                long id = Long.parseLong(nodeElement.getAttribute("id"));
                double latitude = Double.parseDouble(nodeElement.getAttribute("latitude"));
                double longitude = Double.parseDouble(nodeElement.getAttribute("longitude"));
                intersections.put(id, new Intersection(id, latitude, longitude));
                graph.put(id, new ArrayList<>()); // Initialize adjacency list
            }

            // Parse road segments (troncons)
            NodeList tronconList = doc.getElementsByTagName("troncon");
            for (int i = 0; i < tronconList.getLength(); i++) {
                Element tronconElement = (Element) nodeList.item(i);
                long origin = Long.parseLong(tronconElement.getAttribute("origine"));
                long destination = Long.parseLong(tronconElement.getAttribute("destination"));
                double length = Double.parseDouble(tronconElement.getAttribute("longueur"));
                String streetName = tronconElement.getAttribute("nomRue");

                graph.putIfAbsent(origin, new ArrayList<>());

                RoadSegment roadSegment = new RoadSegment(origin, destination, streetName, length);
                graph.get(origin).add(roadSegment);
            }

            cityMap.setIntersections(intersections);
            cityMap.setGraph(graph);

            cityMaps.add(cityMap);

        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML file", e);
        }

        return cityMap;
    }
}
