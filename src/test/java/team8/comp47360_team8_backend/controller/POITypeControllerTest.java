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
import team8.comp47360_team8_backend.model.POIType;
import team8.comp47360_team8_backend.service.POITypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Author : Ryan Davey
 * @Date : 06/07/2025 12:30
 * @Version : V1.0
 * @Description : This file follows formatting and documentation styles initially introduced by Group member - Ze Li.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = POITypeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class POITypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private POITypeService poiTypeService;

    private static final ObjectMapper om = new ObjectMapper();
    
    // This test checks that sending a GET request to /poitypes returns the expected list of POI types in JSON format.
    @Test
    void getAllPOITypesSuccess() throws Exception {
        // Mock Data - 2 simulated POIType objects
        POIType poiType1 = new POIType(1L, "restaurant", null);
        POIType poiType2 = new POIType(2L, "museum", null);
        List<POIType> poiTypes = Arrays.asList(poiType1, poiType2);

        // Returns Mock POIType objects when poiTypeService is called
        when(poiTypeService.getAllPOITypes()).thenReturn(poiTypes);

        // Builds GET request to /poitypes 
        RequestBuilder request = MockMvcRequestBuilders.get("/poitypes").accept(MediaType.APPLICATION_JSON);
        // Performs GET request
        MvcResult result = mockMvc.perform(request).andReturn();

        // Assert that poiTypeService was called exactly once
        verify(poiTypeService, times(1)).getAllPOITypes();

         // Assert that HTTP status is 200 i.e. success
        assertEquals(200, result.getResponse().getStatus());

        
        // 2 strings to allow for comparison of expected vs actual result from the GET request
        String expectedResult = om.writeValueAsString(poiTypes);
        String actualResult = result.getResponse().getContentAsString();
        // Assert these are the expectedResult and actualResult are the same 
        JSONAssert.assertEquals(expectedResult, actualResult, false);
    }

    // This test checks for a successful request but no content (POITypes) returned
    @Test
    void getAllPOITypesEmpty() throws Exception {
        
    // Returns Empty List when poiTypeService is called
    when(poiTypeService.getAllPOITypes()).thenReturn(Collections.emptyList());

         // Builds GET request to /poitypes 
        RequestBuilder request = MockMvcRequestBuilders.get("/poitypes").accept(MediaType.APPLICATION_JSON);
        // Performs GET request
        MvcResult result = mockMvc.perform(request).andReturn();

         // Assert that poiTypeService was called exactly once
        verify(poiTypeService, times(1)).getAllPOITypes();

        // Assert that HTTP status is 200 i.e. success
        assertEquals(200, result.getResponse().getStatus());

        // Assert that response is empty
        String emptyResult = result.getResponse().getContentAsString();
        JSONAssert.assertEquals("[]", emptyResult, false);
    }
}


