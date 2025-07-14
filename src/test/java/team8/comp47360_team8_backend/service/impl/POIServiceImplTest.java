package team8.comp47360_team8_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import team8.comp47360_team8_backend.dto.POIBusynessDistanceRecommendationDTO;
import team8.comp47360_team8_backend.dto.RecommendationInputDTO;
import team8.comp47360_team8_backend.exception.POITypeNotFoundException;
import team8.comp47360_team8_backend.model.POI;
import team8.comp47360_team8_backend.model.POIType;
import team8.comp47360_team8_backend.model.UserPlan;
import team8.comp47360_team8_backend.model.Zone;
import team8.comp47360_team8_backend.repository.POITypeRepository;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * @Author : Ze Li
 * @Date : 03/07/2025 18:32
 * @Version : V1.0
 * @Description :
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class POIServiceImplTest {

    @Mock
    private POITypeRepository poiTypeRepository;

    @Mock
    private ZoneService zoneService;

    @InjectMocks
    @Spy
    private POIServiceImpl poiService = new POIServiceImpl();

    private POIType validPoiType;
    private POI poi1;
    private POI poi2;
    private Zone zone1;
    private Zone zone2;
    private POIType restaurant;
    private POIType park;

    @BeforeEach
    void setUp() {
        zone1 = new Zone(1L, "Zone1");
        zone2 = new Zone(2L, "Zone2");

        restaurant = new POIType(1L, "restaurant", null );
        park = new POIType(2L, "park", null );

        poi1 = new POI(1L, "POI1", "", 53.339428, -6.257664, zone1, restaurant);
        poi2 = new POI(2L, "POI2", "", 53.346093, -6.258860, zone2, park);

    }

    @Test
    void getPOIsByPOITypeName() {
        restaurant.setPOIs(Set.of(poi1));
        when(poiTypeRepository.getByPoiTypeName("restaurant"))
                .thenReturn(Optional.of(restaurant));

        Set<POI> result = poiService.getPOIsByPOITypeName("restaurant");
        assertEquals(1, result.size());
        assertTrue(result.contains(poi1));

        // Non-existent POI type
        when(poiTypeRepository.getByPoiTypeName("nonexistent")).thenReturn(Optional.empty());
        assertThrows(POITypeNotFoundException.class, () -> poiService.getPOIsByPOITypeName("nonexistent"));
    }

    @Test
    void assignBusynessDistanceForPOIs() {

        doReturn(Set.of(poi1, poi2))  // Use doReturn for spies
                .when(poiService)
                .getPOIsByPOITypeName("restaurant");
        // Mock busyness data
        HashMap<Long, String> zoneBusyness = new HashMap<>();
        zoneBusyness.put(1L, "low");    // POI1: busynessScore=10
        zoneBusyness.put(2L, "high");   // POI2: busynessScore=1

        // Last POI is POI1, so distance to POI2 is ~0.76 km
        POI lastPOI = new POI(53.339428, -6.257664); // Same as POI1

        List<POIBusynessDistanceRecommendationDTO> result = poiService
                .assignBusynessDistanceForPOIs("restaurant", lastPOI, zoneBusyness, "walk", 2);

        // POI1 has higher recommendation (same location, busyness=low)
        assertEquals(2, result.size());
        assertEquals("POI1", result.get(0).getPoi().getPoiName());
        assertTrue(result.get(0).getRecommendation() > result.get(1).getRecommendation());

        // Test different transit types
        poiService.assignBusynessDistanceForPOIs("restaurant", lastPOI, zoneBusyness, null, 2);
        poiService.assignBusynessDistanceForPOIs("restaurant", lastPOI, zoneBusyness, "cycle", 2);
        poiService.assignBusynessDistanceForPOIs("restaurant", lastPOI, zoneBusyness, "bus", 2);
        poiService.assignBusynessDistanceForPOIs("restaurant", lastPOI, zoneBusyness, "car", 1);
    }

    @Test
    void getListOfRecommendations() {
        // empty input -> BAD_REQUEST
        ResponseStatusException ex1 = assertThrows(
                ResponseStatusException.class,
                () -> poiService.getListOfRecommendations(Collections.emptyList())
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatusCode());

        // invalid start (missing required start fields)
        RecommendationInputDTO badStart = new RecommendationInputDTO();  // all nulls
        List<RecommendationInputDTO> tooFew = List.of(badStart);
        ResponseStatusException ex2 = assertThrows(
                ResponseStatusException.class,
                () -> poiService.getListOfRecommendations(tooFew)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatusCode());

        // 1) Prepare three inputs: start (fixed POI+time), one flexible-time-only, end (fixed POI+time)
        ZonedDateTime t0 = ZonedDateTime.parse("2025-07-13T09:00:00Z");
        ZonedDateTime t1 = ZonedDateTime.parse("2025-07-13T12:00:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2025-07-13T18:00:00Z");
        RecommendationInputDTO start = new RecommendationInputDTO(
                "Home",  1L, 40.0, -74.0, t0, null, null, null
        );
        RecommendationInputDTO mid = new RecommendationInputDTO(
                null, null,  null,  null,  t1, "walk", "restaurant", null
        );
        RecommendationInputDTO end = new RecommendationInputDTO(
                "Office", 2L, 40.1, -74.1, t2, null, null, null
        );
        List<RecommendationInputDTO> inputs = List.of(start, mid, end);
        getListOfRecommendations_happyPathAndErrors(inputs);
        List<String> transmitTypes = List.of("walk", "cycle", "bus", "car");
        for (String type : transmitTypes) {
            start.setTransitType(type);
            getListOfRecommendations_happyPathAndErrors(inputs);
        }
    }

    @Test
    void getListOfRecommendations_fullyFlexibleFallback() {
        // 1) set up three slots: a fixed start, one fully-flexible, and a fixed end
        ZonedDateTime t0 = ZonedDateTime.parse("2025-07-13T09:00:00Z");
        ZonedDateTime tEnd = ZonedDateTime.parse("2025-07-13T18:00:00Z");
        RecommendationInputDTO start = new RecommendationInputDTO(
                "Home",         // poiName
                1L,             // zoneId
                40.0, -74.0,    // lat/lng
                t0,             // time
                "car",          // transitType
                null,           // poiTypeName
                15              // stayMinutes
        );
        RecommendationInputDTO flexible = new RecommendationInputDTO(
                null,           // poiName (to be filled)
                1L,           // zoneId
                null, null,     // lat/lng
                null,           // time (to be computed)
                "car",          // transitType
                "museum",       // poiTypeName
                30              // stayMinutes
        );
        RecommendationInputDTO end = new RecommendationInputDTO(
                "Office",       // poiName
                2L,             // zoneId
                41.0, -75.0,    // lat/lng
                tEnd,           // time
                null,           // transitType
                null,           // poiTypeName
                15              // stayMinutes
        );

        List<RecommendationInputDTO> inputs = List.of(start, flexible, end);

        // 2) stub zoneService so that predictZoneBusyness(...) always returns some busyness
        when(zoneService.predictZoneBusyness(eq(List.of(t0)), eq(1L)))
                .thenReturn(List.of("low"));
        when(zoneService.predictZoneBusyness(eq(List.of(tEnd)), eq(2L)))
                .thenReturn(List.of("low"));
        // for the fully-flexible midpoint calls we only care about the map, stub with a dummy:
        when(zoneService.predictZoneBusyness(any(ZonedDateTime.class)))
                .thenReturn(new HashMap<>(Map.of(1L, "low", 2L, "high")));

        // 3a) make the “big candidate list” call (limit=100) return a single bad DTO
        // instead of 41, –75, use something extreme:
        POI bad = new POI(
                99L, "BadMuseum", "",
                0.0, 0.0,
                new Zone(99L, "Z99"),
                new POIType(99L, "museum", null)
        );
        POIBusynessDistanceRecommendationDTO badDto =
                new POIBusynessDistanceRecommendationDTO(
                        bad,
                        "low",
                        /*distance*/ 444.0,  // arbitrary
                        /*score*/    0.0
                );
        doReturn(List.of(badDto))
                .when(poiService)
                .assignBusynessDistanceForPOIs(
                        eq("museum"),
                        any(POI.class),
                        any(HashMap.class),    // <— must be any(HashMap.class), not anyMap()
                        eq("car"),
                        eq(100)
                );


        // 3b) stub the fallback call (limit=1) to return a good DTO
        POI good = new POI(42L, "FallbackMuseum", "", 40.5, -74.5, new Zone(42L, "Z42"), new POIType(42L, "museum", null));
        POIBusynessDistanceRecommendationDTO goodDto =
                new POIBusynessDistanceRecommendationDTO(good, "medium", /*distance*/1.0, /*score*/5.0);

        // 3b) fallback list returns the one good DTO:
        doReturn(List.of(goodDto))
                .when(poiService)
                .assignBusynessDistanceForPOIs(
                        eq("museum"),
                        any(POI.class),
                        any(HashMap.class),   // <-- was anyMap()
                        eq("car"),
                        eq(1)
                );



        // 4) run the algorithm
        List<UserPlan> plan = poiService.getListOfRecommendations(inputs);

        // 5) verify that we got exactly three entries and that the middle one is our "good" fallback
        assertEquals(3, plan.size(), "should produce exactly start, fallback, end");

        // start and end should be unchanged
        assertEquals("Home", plan.get(0).getPoiName());
        assertEquals("Office", plan.get(2).getPoiName());

        // the middle entry must be the fallback POI
        UserPlan midPlan = plan.get(1);
        assertEquals("FallbackMuseum", midPlan.getPoiName());
        assertEquals("medium",      midPlan.getBusyness());

        // its time should lie strictly between t0 + stay and tEnd
        ZonedDateTime departStart = t0.plusMinutes(15);
        assertTrue(midPlan.getTime().isAfter(departStart));
        assertTrue(midPlan.getTime().isBefore(tEnd));

        // and because we fell back, it should be near the midpoint:
        long totalWindow = Duration.between(departStart, tEnd).toMinutes();
        long midOffset   = Duration.between(departStart, midPlan.getTime()).toMinutes();
        assertTrue(Math.abs(midOffset - totalWindow/2) < 2, "fallback should be approximately at midpoint");
    }


    void getListOfRecommendations_happyPathAndErrors(List<RecommendationInputDTO> inputs) {
        // 2) Stub zoneService for fixed anchors (start and end)
        when(zoneService.predictZoneBusyness(anyList(), eq(1L)))
                .thenReturn(List.of("medium"));
        when(zoneService.predictZoneBusyness(anyList(), eq(2L)))
                .thenReturn(List.of("low"));

        // 3) Stub zoneService.predictZoneBusyness(ZonedDateTime) for building the busyness-map
        //    used by assignBusynessDistanceForPOIs on the mid entry.
        Map<Long,String> zoneMap = Map.of( 1L,"low", 2L,"high" );
        when(zoneService.predictZoneBusyness(any()))
                .thenReturn(new HashMap<>(zoneMap));

        // 4) Spy/stub the helper to return a single top recommendation for “restaurant”
        POI fakePoi = new POI( 99L, "PizzaPlace", "", 40.005, -74.005, new Zone(1L,"Z1"), null );
        POIBusynessDistanceRecommendationDTO fakeRec =
                new POIBusynessDistanceRecommendationDTO(
                        fakePoi, "low", 0.5,  (10/2.0 + (10*Math.exp(-0.5/2))/2)
                );
        doReturn(List.of(fakeRec))
                .when(poiService)
                .assignBusynessDistanceForPOIs(
                        eq("restaurant"),
                        any(POI.class),
                        eq(new HashMap<>(zoneMap)),
                        isNull(),
                        eq(1)
                );

        // 5) Call under test
        List<UserPlan> plan = poiService.getListOfRecommendations(inputs);
        assertEquals(inputs.size(), plan.size());
    }


    @Test
    void calculateRecommendation() {
        // Busyness: low (10), distance: 1 km, decay=2 → distanceScore=10*exp(-1/2)=6.07
        double expected = (10/2.0) + (6.065306597126334)/2; // ~8.03
        double actual = poiService.calculateRecommendation("low", 1.0, 2);
        assertEquals(expected, actual, 0.01);
    }

    @Test
    void calculateDistance() {
        // Distance between Dublin Spire and Trinity College (~1.3 km)
        POI spire = new POI(53.349764, -6.260273);
        POI trinity = new POI(53.3439, -6.2546);
        double distance = poiService.calculateDistance(spire, trinity);
        assertEquals(0.76, distance, 0.1); // Approximate value
    }
}