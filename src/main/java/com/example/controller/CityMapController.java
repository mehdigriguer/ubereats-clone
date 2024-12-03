package com.example.controller;

import com.example.service.CityMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/city-map")
public class CityMapController {

    @Autowired
    private CityMapService cityMapService;
    /*
    @PostMapping("/upload")
    public Reseau uploadCityMap(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file temporarily
            File tempFile = File.createTempFile("city-map", ".xml");
            file.transferTo(tempFile);

            // Load and parse the XML
            Reseau reseau = cityMapService.loadFromXML(tempFile.getAbsolutePath());

            // Clean up the temporary file
            tempFile.delete();

            return reseau;
        } catch (IOException e) {
            throw new RuntimeException("Error handling the uploaded file", e);
        }
    }
    */
}
