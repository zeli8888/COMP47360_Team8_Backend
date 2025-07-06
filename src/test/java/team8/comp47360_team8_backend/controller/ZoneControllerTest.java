package team8.comp47360_team8_backend.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ZoneControllerTest {

    @Mock
    private ZoneService zoneService;

    @InjectMocks
    private ZoneController zoneController;

    public ZoneControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getZoneBusyness() {
        // Arrange
        Long zoneId = 42L;
        ZonedDateTime now = ZonedDateTime.now();
        int predictedHours = 3;

        List<String> mockBusyness = Arrays.asList("Low", "Medium", "High");

        when(zoneService.predictZoneBusyness(anyList(), eq(zoneId)))
                .thenReturn(mockBusyness);

        // Act
        ResponseEntity<List<String>> response = zoneController.getZoneBusyness(zoneId, now, predictedHours);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockBusyness, response.getBody());
        verify(zoneService, times(1)).predictZoneBusyness(anyList(), eq(zoneId));
    }
}
