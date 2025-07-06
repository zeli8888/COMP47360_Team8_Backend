package team8.comp47360_team8_backend.dto;

import org.junit.jupiter.api.Test;
import team8.comp47360_team8_backend.model.POI;

import static org.junit.jupiter.api.Assertions.*;

class POIBusynessDistanceRecommendationDTOTest {

    @Test
    void testConstructorAndGettersSetters() {
        POI poi = new POI(1L, "Library", "City library", 53.3498, -6.2603, null, null);

        POIBusynessDistanceRecommendationDTO dto = new POIBusynessDistanceRecommendationDTO();
        dto.setPoi(poi);
        dto.setBusyness("Low");
        dto.setDistance(1.5);
        dto.setRecommendation(8.5);

        assertEquals(poi, dto.getPoi());
        assertEquals("Low", dto.getBusyness());
        assertEquals(1.5, dto.getDistance());
        assertEquals(8.5, dto.getRecommendation());
    }

    @Test
    void testAllArgsConstructor() {
        POI poi = new POI(2L, "Museum", "Art museum", 53.344, -6.267, null, null);
        POIBusynessDistanceRecommendationDTO dto = new POIBusynessDistanceRecommendationDTO(poi, "Medium", 2.2, 6.3);

        assertEquals(poi, dto.getPoi());
        assertEquals("Medium", dto.getBusyness());
        assertEquals(2.2, dto.getDistance());
        assertEquals(6.3, dto.getRecommendation());
    }
}
