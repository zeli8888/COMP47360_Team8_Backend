package team8.comp47360_team8_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * @Author : Ze Li
 * @Date : 28/06/2025 23:40
 * @Version : V1.0
 * @Description :
 */

@RestController
public class WeatherController {
    @Value("${weather.url}")
    private String weatherUrl;
    @Value("${weather.key}")
    private String weatherKey;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/weather")
    public ResponseEntity<String> getWeatherData(@RequestParam(required = true) double latitude,
                                                 @RequestParam(required = true) double longitude) {
        String url = UriComponentsBuilder.fromUriString(weatherUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", weatherKey)
                .queryParam("exclude", "minutely").toUriString();

        String jsonResponse = restTemplate.getForObject(url, String.class);

        return ResponseEntity.ok(jsonResponse);
    }
}
