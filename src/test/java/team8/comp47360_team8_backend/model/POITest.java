package team8.comp47360_team8_backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class POITest {

    @Test
    void testPOIConstructorAndGettersSetters() {
        // Arrange
        Zone zone = new Zone();
        POIType poiType = new POIType();
        Long id = 1L;
        String name = "Library";
        String description = "Public Library";
        double latitude = 53.3498;
        double longitude = -6.2603;

        // Act
        POI poi = new POI(id, name, description, latitude, longitude, zone, poiType);

        // Assert
        assertEquals(id, poi.getPoiId());
        assertEquals(name, poi.getPoiName());
        assertEquals(description, poi.getPoiDescription());
        assertEquals(latitude, poi.getLatitude());
        assertEquals(longitude, poi.getLongitude());
        assertEquals(zone, poi.getZone());
        assertEquals(poiType, poi.getPoiType());

        // Modify and re-test setters
        poi.setPoiName("Museum");
        poi.setPoiDescription("Art Museum");
        poi.setLatitude(40.7128);
        poi.setLongitude(-74.0060);

        assertEquals("Museum", poi.getPoiName());
        assertEquals("Art Museum", poi.getPoiDescription());
        assertEquals(40.7128, poi.getLatitude());
        assertEquals(-74.0060, poi.getLongitude());

        poi.setZone(new Zone());
        poi.setPoiType(new POIType());
        poi.setPoiId(2L);
        poi.setUserRating(4.0F);
        assertEquals(4.0F, poi.getUserRating(), 0.1);
        poi.setPictureUrl("picture2.jpg");
        assertEquals("picture2.jpg", poi.getPictureUrl());
    }

    @Test
    void testMinimalConstructor() {
        POI poi = new POI(34.05, -118.25);
        assertEquals(34.05, poi.getLatitude());
        assertEquals(-118.25, poi.getLongitude());
    }

    @Test
    void testNoArgsConstructor() {
        POI poi = new POI();
    }

    @Test
    void testAllArgsConstructor() {
        POI poi = new POI(1L, "Library", "Public Library", 53.3498, -6.2603, 5.0F, "picture.jpg", null, null);
        assertEquals(5.0F, poi.getUserRating(), 0.1);
        assertEquals("picture.jpg", poi.getPictureUrl());
    }
}
