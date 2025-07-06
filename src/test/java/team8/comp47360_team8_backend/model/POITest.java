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
    }

    @Test
    void testMinimalConstructor() {
        POI poi = new POI(34.05, -118.25);
        assertEquals(34.05, poi.getLatitude());
        assertEquals(-118.25, poi.getLongitude());
    }
}
