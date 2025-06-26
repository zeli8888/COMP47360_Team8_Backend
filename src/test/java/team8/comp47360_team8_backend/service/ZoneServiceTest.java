package team8.comp47360_team8_backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team8.comp47360_team8_backend.model.Zone;
import team8.comp47360_team8_backend.repository.ZoneRepository;
import team8.comp47360_team8_backend.service.impl.ZoneServiceImpl;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

/**
 * @Author : Ze Li
 * @Date : 19/06/2025 15:04
 * @Version : V1.0
 * @Description :
 */
@ExtendWith(SpringExtension.class)
class ZoneServiceTest {
    @Mock
    private ZoneRepository zoneRepository;

    @InjectMocks
    private ZoneService zoneService = new ZoneServiceImpl();

    @Test
    void predictZoneBusyness() {
        // Arrange
        ZonedDateTime dateTime = ZonedDateTime.now();
        List<Zone> zones = Arrays.asList(
                new Zone(1L, "Zone1"),
                new Zone(2L, "Zone2"),
                new Zone(3L, "Zone3"));
        when(zoneRepository.findAllByOrderByZoneIdAsc()).thenReturn(zones);

        // Act
        HashMap<Long, String> result = zoneService.predictZoneBusyness(dateTime);

        // Assert
        HashMap<Long, String> expected = new HashMap<>();
        expected.put(1L, "low");
        expected.put(2L, "medium");
        expected.put(3L, "high");

        assertEquals(expected, result);
        verify(zoneRepository, times(1)).findAllByOrderByZoneIdAsc();
    }
}