package team8.comp47360_team8_backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZoneTest {

    @Test
    void testDefaultConstructorAndSetters() {
        Zone zone = new Zone();
        zone.setZoneId(10L);
        zone.setZoneName("North Zone");

        assertEquals(10L, zone.getZoneId());
        assertEquals("North Zone", zone.getZoneName());
    }

    @Test
    void testParameterizedConstructor() {
        Zone zone = new Zone(5L, "Central Zone");

        assertEquals(5L, zone.getZoneId());
        assertEquals("Central Zone", zone.getZoneName());
    }
}
