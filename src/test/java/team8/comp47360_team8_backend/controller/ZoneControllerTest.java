package team8.comp47360_team8_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = ZoneController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ZoneControllerTest {

    @MockitoBean
    private ZoneService zoneService;

    @Autowired
    private ZoneController zoneController;

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
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockBusyness, response.getBody());
        verify(zoneService, times(1)).predictZoneBusyness(anyList(), eq(zoneId));

        // predictedHours is null
        ResponseEntity<List<String>> response2 = zoneController.getZoneBusyness(zoneId, now, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllZoneBusyness() {
        ZonedDateTime now = ZonedDateTime.now();
        HashMap<Long, String> zoneBusynessMap = new HashMap<>();
        zoneBusynessMap.put(1L, "low");
        zoneBusynessMap.put(2L, "medium");
        zoneBusynessMap.put(3L, "high");
        when(zoneService.predictZoneBusyness(any(ZonedDateTime.class))).thenReturn(zoneBusynessMap);

        ResponseEntity<HashMap<Long, String>> response = zoneController.getZoneBusyness(now);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(zoneBusynessMap, response.getBody());
    }
}
