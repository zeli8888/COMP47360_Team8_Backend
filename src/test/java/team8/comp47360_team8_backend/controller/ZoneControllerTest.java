package team8.comp47360_team8_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.ZonedDateTime;
import java.util.Arrays;
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
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockBusyness, response.getBody());
        verify(zoneService, times(1)).predictZoneBusyness(anyList(), eq(zoneId));
    }
}
