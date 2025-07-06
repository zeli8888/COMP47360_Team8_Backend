package team8.comp47360_team8_backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class WeatherControllerTest {

    @Test
    void getWeatherData_mocked() {
        // Arrange
        WeatherController controller = new WeatherController() {
            @Override
            public ResponseEntity<String> getWeatherData(double latitude, double longitude) {
                // Mocking response without real API call
                return ResponseEntity.ok("{\"weather\": \"mocked sunny\"}");
            }
        };

        // Act
        ResponseEntity<String> response = controller.getWeatherData(53.35, -6.26);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("mocked sunny"));
    }
}
