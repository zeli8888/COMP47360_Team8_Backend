package team8.comp47360_team8_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import team8.comp47360_team8_backend.repository.ZoneRepository;

import javax.naming.ServiceUnavailableException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


/**
 * @Author : Ze Li
 * @Date : 03/07/2025 18:33
 * @Version : V1.0
 * @Description :
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ZoneServiceImplTest {

    @Mock
    private ZoneRepository zoneRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ZoneServiceImpl zoneService = new ZoneServiceImpl();
    private List<Long> zoneIds = List.of(1L, 2L, 3L);
    private List<ZonedDateTime> zonedDateTimeList = List.of(ZonedDateTime.now(), ZonedDateTime.now().plusHours(1));

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(zoneService, "restTemplate", restTemplate);
    }

    @Test
    void predictZoneBusyness() {
        when(zoneRepository.findDistinctZoneIds()).thenReturn(zoneIds);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("busyness", List.of("Low", "Medium", "High")), HttpStatus.OK));

        HashMap<Long, String> result = zoneService.predictZoneBusyness(ZonedDateTime.now());
        assertEquals(3, result.size());
        assertEquals("Low", result.get(1L));
        assertEquals("Medium", result.get(2L));
        assertEquals("High", result.get(3L));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(RuntimeException.class);
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> zoneService.predictZoneBusyness(ZonedDateTime.now()));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseStatusException.getStatusCode());
    }

    @Test
    void testPredictZoneBusyness() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("busyness", List.of("Low", "Medium")), HttpStatus.OK));

        List<String> result = zoneService.predictZoneBusyness(zonedDateTimeList, 1L);
        assertEquals(2, result.size());
        assertEquals("Low", result.get(0));
        assertEquals("Medium", result.get(1));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(RuntimeException.class);
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> zoneService.predictZoneBusyness(zonedDateTimeList, 1L));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseStatusException.getStatusCode());
    }

}