package team8.comp47360_team8_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team8.comp47360_team8_backend.model.POI;
import team8.comp47360_team8_backend.service.POIService;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 22:27
 * @Version : V1.0
 * @Description :
 */

@RestController
public class POIController {

    @Autowired
    private POIService poiService;

    @GetMapping("/pois")
    public ResponseEntity<Set<POI>> getPOIsByPOITypeName(@RequestBody POI lastPOI, @RequestParam(required = true) String poiTypeName,
                                                         @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        return ResponseEntity.ok(poiService.getPOIsByPOITypeName(poiTypeName));
    }
}
