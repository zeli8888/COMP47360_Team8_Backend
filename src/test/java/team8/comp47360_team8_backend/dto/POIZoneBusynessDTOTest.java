package team8.comp47360_team8_backend.dto;

import org.junit.jupiter.api.Test;
import team8.comp47360_team8_backend.model.POI;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class POIZoneBusynessDTOTest {

    @Test
    void testConstructorAndGettersSetters() {
        // Prepare test data
        POI poi = new POI(1L, "Test POI", "Description", 10.0, 20.0, null, null);
        POIBusynessDistanceRecommendationDTO dto = new POIBusynessDistanceRecommendationDTO(poi, "Low", 1.5, 0.9);
        List<POIBusynessDistanceRecommendationDTO> dtoList = List.of(dto);

        HashMap<Long, String> zoneMap = new HashMap<>();
        zoneMap.put(101L, "Moderate");

        // Create DTO using constructor
        POIZoneBusynessDTO zoneDto = new POIZoneBusynessDTO(dtoList, zoneMap);

        // Assertions
        assertEquals(dtoList, zoneDto.getBusynessDistanceRecommendationDTOS());
        assertEquals(zoneMap, zoneDto.getZoneBusynessMap());

        // Set new values
        POIBusynessDistanceRecommendationDTO dto2 = new POIBusynessDistanceRecommendationDTO(poi, "High", 3.0, 0.5);
        List<POIBusynessDistanceRecommendationDTO> newDtoList = List.of(dto2);

        HashMap<Long, String> newZoneMap = new HashMap<>();
        newZoneMap.put(202L, "Busy");

        zoneDto.setBusynessDistanceRecommendationDTOS(newDtoList);
        zoneDto.setZoneBusynessMap(newZoneMap);

        // Re-assert after setters
        assertEquals(newDtoList, zoneDto.getBusynessDistanceRecommendationDTOS());
        assertEquals(newZoneMap, zoneDto.getZoneBusynessMap());
    }
}
