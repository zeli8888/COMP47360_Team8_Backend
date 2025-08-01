package team8.comp47360_team8_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import team8.comp47360_team8_backend.repository.ZoneRepository;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author : Ze Li
 * @Date : 03/06/2025 11:56
 * @Version : V1.0
 * @Description :
 */

@Service
public class ZoneServiceImpl implements ZoneService {
    @Value("${ml.url}")
    private String ML_URL;
    @Autowired
    private ZoneRepository zoneRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private String formatDateTime(ZonedDateTime dateTime) {
        Instant instant = dateTime
                .withZoneSameInstant(ZoneOffset.UTC)
                .toInstant()
                .truncatedTo(ChronoUnit.MICROS); // 1 ms = 1e-6 s

        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    @Override
    public HashMap<Long, String> predictZoneBusyness(ZonedDateTime dateTime) {
        List<Long> zoneIds = zoneRepository.findDistinctZoneIds();
        HashMap<Long, String> zoneBusynessMap = new HashMap<>();

        try {
            // Prepare request body
            HashMap<String, Object> requestBody = new HashMap<>();
            requestBody.put("zoneIds", zoneIds);
            requestBody.put("dateTime", formatDateTime(dateTime));

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<HashMap<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Send POST request
            ResponseEntity<Map> response = restTemplate.exchange(
                    ML_URL + "predict_multi_zones",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<String> busynessList = (List<String>) responseBody.get("busyness");

                // Match zone IDs with corresponding busyness values
                for (int i = 0; i < zoneIds.size(); i++) {
                    zoneBusynessMap.put(zoneIds.get(i), busynessList.get(i));
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to predict busyness, please try again later.");
        }
        return zoneBusynessMap;
    }

    @Override
    public List<String> predictZoneBusyness(List<ZonedDateTime> zonedDateTimeList, Long zoneId) {
        List<String> busynessList = new ArrayList<>();

        try {
            HashMap<String, Object> requestBody = new HashMap<>();
            requestBody.put("zoneId", zoneId);
            requestBody.put("zonedDateTimeList", zonedDateTimeList.stream()
                    .map(this::formatDateTime)
                    .collect(Collectors.toList()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<HashMap<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    ML_URL + "predict_single_zone",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                busynessList = (List<String>) responseBody.get("busyness");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to predict busyness, please try again later.");
        }

        return busynessList;
    }
}
