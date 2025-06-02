package team8.comp47360_team8_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import team8.comp47360_team8_backend.model.POI;
import team8.comp47360_team8_backend.model.POIType;
import team8.comp47360_team8_backend.model.Zone;
import team8.comp47360_team8_backend.repository.POIRepository;
import team8.comp47360_team8_backend.repository.POITypeRepository;
import team8.comp47360_team8_backend.repository.ZoneRepository;

import java.util.HashSet;

@SpringBootTest
class Comp47360Team8BackendApplicationTests {
    @Autowired
    private POIRepository poiRepository;

    @Test
    void contextLoads() {
    }

    @Test
    @Transactional
    @Rollback(false)
    void initTestData() {
        POIType park = new POIType(null, "park", new HashSet<>());
        POIType restaurant = new POIType(null, "restaurant", new HashSet<>());

        Zone zone1 = new Zone(1L, "zone1", 3L);
        Zone zone2 = new Zone(2L, "zone2", 3L);

        POI park1 = new POI(null, "park1", "park1 desc", 1.0, 1.0, zone1, park);
        POI park2 = new POI(null, "park2", "park2 desc", 1.0, 1.0, zone1, park);
        POI park3 = new POI(null, "park3", "park3 desc", 1.0, 1.0, zone2, park);
        POI restaurant1 = new POI(null, "restaurant1", "restaurant1 desc", 1.0, 1.0, zone1, restaurant);
        POI restaurant2 = new POI(null, "restaurant2", "restaurant2 desc", 1.0, 1.0, zone2, restaurant);
        POI restaurant3 = new POI(null, "restaurant3", "restaurant3 desc", 1.0, 1.0, zone2, restaurant);

        poiRepository.save(park1);
        poiRepository.save(park2);
        poiRepository.save(park3);
        poiRepository.save(restaurant1);
        poiRepository.save(restaurant2);
        poiRepository.save(restaurant3);
    }
}
