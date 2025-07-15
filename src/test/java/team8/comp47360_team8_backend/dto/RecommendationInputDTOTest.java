package team8.comp47360_team8_backend.dto;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationInputDTOTest {

    @Test
    void testConstructorAndGettersSetters() {
        // Given
        String poiName = "Library";
        Long zoneId = 101L;
        Double latitude = 53.3498;
        Double longitude = -6.2603;
        ZonedDateTime time = ZonedDateTime.now();
        String transitType = "walk";
        String poiTypeName = "Study Area";
        Integer StayMinutes = 30;

        // When using all-args constructor
        RecommendationInputDTO dto = new RecommendationInputDTO(poiName, zoneId, latitude, longitude, time, transitType, poiTypeName, StayMinutes);

        // Then
        assertEquals(poiName, dto.getPoiName());
        assertEquals(zoneId, dto.getZoneId());
        assertEquals(latitude, dto.getLatitude());
        assertEquals(longitude, dto.getLongitude());
        assertEquals(time, dto.getTime());
        assertEquals(transitType, dto.getTransitType());
        assertEquals(poiTypeName, dto.getPoiTypeName());
        assertEquals(StayMinutes, dto.getStayMinutes());

        // When using setters
        RecommendationInputDTO emptyDto = new RecommendationInputDTO();
        emptyDto.setPoiName("Café");
        emptyDto.setZoneId(202L);
        emptyDto.setLatitude(40.7128);
        emptyDto.setLongitude(-74.0060);
        emptyDto.setTime(time);
        emptyDto.setTransitType("bus");
        emptyDto.setPoiTypeName("Food");
        emptyDto.setStayMinutes(40);

        // Then
        assertEquals("Café", emptyDto.getPoiName());
        assertEquals(202L, emptyDto.getZoneId());
        assertEquals(40.7128, emptyDto.getLatitude());
        assertEquals(-74.0060, emptyDto.getLongitude());
        assertEquals(time, emptyDto.getTime());
        assertEquals("bus", emptyDto.getTransitType());
        assertEquals("Food", emptyDto.getPoiTypeName());
        assertEquals(40, emptyDto.getStayMinutes());
    }
}
