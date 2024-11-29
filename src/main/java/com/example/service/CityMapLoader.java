package com.example.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;

@Service
public class CityMapLoader {

    public Reseau loadCityMap(String filePath) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Reseau.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Reseau) unmarshaller.unmarshal(new File(filePath));
    }
}