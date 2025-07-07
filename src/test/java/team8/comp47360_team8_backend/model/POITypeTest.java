package team8.comp47360_team8_backend.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class POITypeTest {

    @Test
    void testDefaultConstructorAndSetters() {
        POIType poiType = new POIType();

        poiType.setPoiTypeID(1L);
        poiType.setPoiTypeName("Museum");

        assertEquals(1L, poiType.getPoiTypeID());
        assertEquals("Museum", poiType.getPoiTypeName());
    }

    @Test
    void testParameterizedConstructor() {
        Set<POI> poiSet = new HashSet<>();
        POI poi = new POI(53.35, -6.26);
        poiSet.add(poi);

        POIType poiType = new POIType(2L, "Park", poiSet);

        assertEquals(2L, poiType.getPoiTypeID());
        assertEquals("Park", poiType.getPoiTypeName());
        assertEquals(1, poiType.getPOIs().size());
    }

    @Test
    void testSetAndGetPOIs() {
        POIType poiType = new POIType();
        Set<POI> poiSet = new HashSet<>();
        POI poi = new POI(51.50, -0.12);
        poiSet.add(poi);

        poiType.setPOIs(poiSet);
        assertEquals(1, poiType.getPOIs().size());
    }
}
