package team8.comp47360_team8_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    List<RecommendationInputDTO> resetInputForListOfRecommendations() {
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
        return List.of(start, mid, end);
    }

    @Test
    void getListOfRecommendations() {
        // empty input -> BAD_REQUEST
        ResponseStatusException ex1 = assertThrows(
                ResponseStatusException.class,
                () -> poiService.getListOfRecommendations(Collections.emptyList())
        );

        // invalid start (missing required start fields)
        List<RecommendationInputDTO> invalidStartInputs = resetInputForListOfRecommendations();
        invalidStartInputs.get(0).setLatitude(null);
        ResponseStatusException ex2 = assertThrows(
                ResponseStatusException.class,
                () -> poiService.getListOfRecommendations(invalidStartInputs)
        );

        // unfixed POI without poi type -> BAD_REQUEST
        List<RecommendationInputDTO> inputs = resetInputForListOfRecommendations();
        inputs.get(1).setPoiTypeName(null);
        List<RecommendationInputDTO> errorInputs = inputs;
        assertThrows(ResponseStatusException.class, () -> poiService.getListOfRecommendations(errorInputs));

        // end POI after 21:00 -> BAD_REQUEST
        List<RecommendationInputDTO> invalidEndInputs = resetInputForListOfRecommendations();
        invalidEndInputs.get(2).setTime(ZonedDateTime.parse("2025-07-13T22:00:00Z"));
        assertThrows(ResponseStatusException.class, () -> poiService.getListOfRecommendations(invalidEndInputs));

        getListOfRecommendationsNormal(resetInputForListOfRecommendations());

        // different transit types
        inputs = resetInputForListOfRecommendations();
        List<String> transmitTypes = List.of("walk", "cycle", "bus", "car");
        for (String type : transmitTypes) {
            inputs.get(0).setTransitType(type);
            inputs.get(1).setTime(null);
            inputs.get(2).setTime(null);
            getListOfRecommendationsNormal(inputs);
        }

        // end location has fixed time without fixed location
        inputs = resetInputForListOfRecommendations();
        inputs.get(2).setLongitude(null);
        inputs.get(2).setPoiTypeName("park");
        getListOfRecommendationsNormal(inputs);

        // fixed poi with large stay time
        inputs = resetInputForListOfRecommendations();
        RecommendationInputDTO poiInput = inputs.get(2);
        poiInput.setLongitude(40.2);
        poiInput.setLatitude(-74.2);
        poiInput.setZoneId(3L);
        poiInput.setPoiTypeName("restaurant");
        poiInput.setStayMinutes(600);
        poiInput.setTime(null);
        getListOfRecommendationsNormal(inputs);

        // flexible poi with large stay time
        inputs = resetInputForListOfRecommendations();
        poiInput = inputs.get(2);
        poiInput.setLongitude(40.2);
        poiInput.setLatitude(-74.2);
        poiInput.setZoneId(null);
        poiInput.setPoiTypeName("restaurant");
        poiInput.setStayMinutes(600);
        poiInput.setTime(null);
        getListOfRecommendationsNormal(inputs);

        // fixed time middle poi closer to end
        inputs = resetInputForListOfRecommendations();
        inputs.get(1).setTime(ZonedDateTime.parse("2025-07-13T17:00:00Z"));
        getListOfRecommendationsNormal(inputs);

        // totally fixed middle poi
        inputs = resetInputForListOfRecommendations();
        inputs.get(1).setPoiName("middle Poi");
        inputs.get(1).setZoneId(1L);
        inputs.get(1).setLongitude(40.1);
        inputs.get(1).setLatitude(-74.1);
        getListOfRecommendationsNormal(inputs);
    }

    void getListOfRecommendationsNormal(List<RecommendationInputDTO> inputs) {
        when(zoneService.predictZoneBusyness(anyList(), any()))
                .thenReturn(List.of("medium"));
        Map<Long,String> zoneMap = Map.of( 1L,"low", 2L,"high" );
        when(zoneService.predictZoneBusyness(any()))
                .thenReturn(new HashMap<>(zoneMap));

        POI fakePoi = new POI( 99L, "PizzaPlace", "", 40.005, -74.005, new Zone(1L,"Z1"), null );
        POIBusynessDistanceRecommendationDTO fakeRec =
                new POIBusynessDistanceRecommendationDTO(
                        fakePoi, "low", 0.5,  (10/2.0 + (10*Math.exp(-0.5/2))/2)
                );
        doReturn(List.of(fakeRec))
                .when(poiService)
                .assignBusynessDistanceForPOIs(
                        any(),
                        any(POI.class),
                        any(),
                        any(),
                        anyInt()
                );

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