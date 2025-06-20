package team8.comp47360_team8_backend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import team8.comp47360_team8_backend.dto.POIBusynessDistanceDTO;
import team8.comp47360_team8_backend.model.POI;
import team8.comp47360_team8_backend.model.POIType;
import team8.comp47360_team8_backend.model.Zone;
import team8.comp47360_team8_backend.service.POIService;
import team8.comp47360_team8_backend.service.ZoneService;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @Author : Ze Li
 * @Date : 19/06/2025 15:40
 * @Version : V1.0
 * @Description :
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = POIController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class POIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private POIService poiService;

    @MockitoBean
    private ZoneService zoneService;

    private static final ObjectMapper om = new ObjectMapper();

    private double calculateDistance(POI poi1, POI poi2) {
        // Radius of the Earth in kilometers
        final double R = 6371.0;
        double lat1 = Math.toRadians(poi1.getLatitude());
        double lon1 = Math.toRadians(poi1.getLongitude());
        double lat2 = Math.toRadians(poi2.getLatitude());
        double lon2 = Math.toRadians(poi2.getLongitude());
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @Test
    void getPOIsByPOITypeName() throws Exception {
        // Arrange
        String poiTypeName = "restaurant";
        String dateTimeString = ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        POI lastPOI = new POI(
                null, "lastPOI", "lastPOI description", 5.0, 5.0, null, null
        );

        Zone zone1 = new Zone(1L, "Zone1");
        Zone zone2 = new Zone(2L, "Zone2");

        HashMap<Long, Double> zoneBusynessMap = new HashMap<>();
        zoneBusynessMap.put(1L, 0.6);
        zoneBusynessMap.put(2L, 0.4);

        POIType poiType1 = new POIType(1L, "restaurant", new HashSet<>());

        POI poi1 = new POI(1L, "restaurant1", "description1", 1.0, 1.0, zone1, poiType1);
        POI poi2 = new POI(2L, "restaurant2", "description2", 2.0, 2.0, zone2, poiType1);

        POIBusynessDistanceDTO poiBusynessDistanceDTO1 = new POIBusynessDistanceDTO(
                poi1, 0.6, calculateDistance(lastPOI, poi1)
        );
        POIBusynessDistanceDTO poiBusynessDistanceDTO2 = new POIBusynessDistanceDTO(
                poi2, 0.4, calculateDistance(lastPOI, poi2)
        );

        List<POIBusynessDistanceDTO> expectedResponse = new ArrayList<>();
        expectedResponse.add(poiBusynessDistanceDTO1);
        expectedResponse.add(poiBusynessDistanceDTO2);

        // Mocking the POIService and ZoneService methods
        when(zoneService.predictZoneBusyness(ZonedDateTime.parse(dateTimeString))).thenReturn(zoneBusynessMap);
        when(poiService.getPOIsByPOITypeName(poiTypeName)).thenReturn(Set.of(poi1, poi2));
        when(poiService.assignBusynessDistanceForPOIs(any(), any(), eq(zoneBusynessMap))).thenReturn(expectedResponse);

        // Act and Assert
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/pois")
                .param("poiTypeName", poiTypeName)
                .param("dateTime", dateTimeString)
                .content(om.writeValueAsString(lastPOI))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        verify(zoneService, times(1)).predictZoneBusyness(ZonedDateTime.parse(dateTimeString));
        verify(poiService, times(1)).getPOIsByPOITypeName(poiTypeName);
        verify(poiService, times(1)).assignBusynessDistanceForPOIs(any(), any(), eq(zoneBusynessMap));
        JSONAssert.assertEquals(om.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString(), false);
    }
}