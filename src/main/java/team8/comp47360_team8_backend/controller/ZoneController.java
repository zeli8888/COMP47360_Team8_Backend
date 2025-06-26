package team8.comp47360_team8_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 26/06/2025 21:35
 * @Version : V1.0
 * @Description :
 */
@RestController
public class ZoneController {
    @Autowired
    private ZoneService zoneService;

    @GetMapping("/zones/{zoneId}")
    public ResponseEntity<List<String>> getZoneBusyness(@PathVariable Long zoneId,
                                                @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTime,
                                                @RequestParam(required = false) Integer predictedHours
    ) {
        if (predictedHours == null) predictedHours = 5;
        List<ZonedDateTime> futureDateTimes = new ArrayList<>();
        for (int i = 0; i < predictedHours; i++) {
            futureDateTimes.add(dateTime.plusHours(i));
        }
        List<String> busyness = zoneService.predictZoneBusyness(futureDateTimes, zoneId);
        return ResponseEntity.ok(busyness);
    }
}
