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
import team8.comp47360_team8_backend.dto.POIBusynessDistanceRecommendationDTO;
import team8.comp47360_team8_backend.dto.POIZoneBusynessDTO;
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

    @Test
    void getPOIsByPOITypeName() throws Exception {
        // Mock input
        String poiTypeName = "restaurant";
        String transitType = "car";
        String dateTimeString = ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        POI lastPOI = new POI(
                null, "lastPOI", "lastPOI description", 5.0, 5.0, null, null
        );

        // Mock data
        Zone zone1 = new Zone(1L, "Zone1");
        Zone zone2 = new Zone(2L, "Zone2");
        HashMap<Long, String> zoneBusynessMap = new HashMap<>();
        zoneBusynessMap.put(1L, "low");
        zoneBusynessMap.put(2L, "medium");
        POIType poiType1 = new POIType(1L, "restaurant", new HashSet<>());
        POI poi1 = new POI(1L, "restaurant1", "description1", 1.0, 1.0, zone1, poiType1);
        POI poi2 = new POI(2L, "restaurant2", "description2", 2.0, 2.0, zone2, poiType1);

        // Mock response
        POIBusynessDistanceRecommendationDTO poiBusynessDistanceRecommendationDTO1 = new POIBusynessDistanceRecommendationDTO(
                poi1, "low", 0.123, 8.1
        );
        POIBusynessDistanceRecommendationDTO poiBusynessDistanceRecommendationDTO2 = new POIBusynessDistanceRecommendationDTO(
                poi2, "medium", 0.234, 3.2
        );
        List<POIBusynessDistanceRecommendationDTO> poiBusynessDistanceRecommendationDTOS = new ArrayList<>();
        poiBusynessDistanceRecommendationDTOS.add(poiBusynessDistanceRecommendationDTO1);
        poiBusynessDistanceRecommendationDTOS.add(poiBusynessDistanceRecommendationDTO2);
        POIZoneBusynessDTO expectedResponse = new POIZoneBusynessDTO(poiBusynessDistanceRecommendationDTOS, zoneBusynessMap);

        // Mocking the POIService and ZoneService methods
        when(zoneService.predictZoneBusyness(ZonedDateTime.parse(dateTimeString))).thenReturn(zoneBusynessMap);
        when(poiService.assignBusynessDistanceForPOIs(eq(poiTypeName), any(), eq(zoneBusynessMap), eq("car"), eq(1000))).thenReturn(poiBusynessDistanceRecommendationDTOS);

        // Act and Assert
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/pois")
                .param("poiTypeName", poiTypeName)
                .param("transitType", transitType)
                .param("dateTime", dateTimeString)
                .param("limit", "1000")
                .content(om.writeValueAsString(lastPOI))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        verify(zoneService, times(1)).predictZoneBusyness(ZonedDateTime.parse(dateTimeString));
        verify(poiService, times(1)).assignBusynessDistanceForPOIs(any(), any(), eq(zoneBusynessMap), eq("car"), eq(1000));
        JSONAssert.assertEquals(om.writeValueAsString(expectedResponse), mvcResult.getResponse().getContentAsString(), false);
    }

    @Test
    void getListOfRecommendations() {
    }
}