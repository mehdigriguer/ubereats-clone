package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/delivery/requests")
public class DeliveryController {

    @PostMapping
    public ResponseEntity<String> createRequest(@RequestBody Map<String, Integer> request) {
        if (request == null || !request.containsKey("warehouseId") ||
                !request.containsKey("pickupId") || !request.containsKey("deliveryId")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: missing required fields.");
        }

        Integer warehouseId = request.get("warehouseId");
        Integer pickupId = request.get("pickupId");
        Integer deliveryId = request.get("deliveryId");

        if (warehouseId == null || pickupId == null || deliveryId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: IDs cannot be null.");
        }

        System.out.println("Received IDs: " + warehouseId + ", " + pickupId + ", " + deliveryId);

        // Here, you could save these IDs in the database or process them further.
        return ResponseEntity.ok("Request created successfully.");
    }
}


