package team8.comp47360_team8_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team8.comp47360_team8_backend.dto.POIBusynessDistanceRecommendationDTO;
import team8.comp47360_team8_backend.dto.POIZoneBusynessDTO;
import team8.comp47360_team8_backend.dto.RecommendationInputDTO;
import team8.comp47360_team8_backend.model.POI;
import team8.comp47360_team8_backend.model.UserPlan;
import team8.comp47360_team8_backend.service.POIService;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;

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

    @Autowired
    private ZoneService zoneService;

    @GetMapping("/pois")
    public ResponseEntity<POIZoneBusynessDTO> getPOIsByPOITypeName(@RequestBody POI lastPOI,
                                                                   @RequestParam(required = true) String poiTypeName,
                                                                   @RequestParam(required = false) String transitType,
                                                                   @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTime) {
        HashMap<Long, String> zoneBusynessMap = zoneService.predictZoneBusyness(dateTime);
        List<POIBusynessDistanceRecommendationDTO> poiBusynessDistanceRecommendationDTOS = poiService.assignBusynessDistanceForPOIs(poiTypeName, lastPOI, zoneBusynessMap, transitType);
        // To avoid returning too many useless POIs, we only return the first 1000 most recommendations.
        return ResponseEntity.ok(new POIZoneBusynessDTO(
                poiBusynessDistanceRecommendationDTOS.subList(0, Math.min(1000, poiBusynessDistanceRecommendationDTOS.size())),
                zoneBusynessMap)
        );
    }

    @GetMapping("/pois/recommendation")
    public ResponseEntity<List<UserPlan>> getListOfRecommendations(@RequestBody List<RecommendationInputDTO> recommendationInputDTOS) {
        // the first recommendationInputDTO should be the start location, with valid zoneId, latitude, longitude and time
        // other recommendationInputDTOs should have valid poiTypeName, transitType and time so that we can recommend
        return ResponseEntity.ok(poiService.getListOfRecommendations(recommendationInputDTOS)); }
}
