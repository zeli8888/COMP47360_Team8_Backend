package team8.comp47360_team8_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = WeatherController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class WeatherControllerTest {

    @Mock
    private RestTemplate restTemplateMock;

    @Autowired
    private WeatherController weatherController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getWeatherData_mocked() throws Exception {
        String mockResponse = "{\"temperature\": 25}";
        ReflectionTestUtils.setField(weatherController, "restTemplate", restTemplateMock);
        when(restTemplateMock.getForObject(anyString(), any())).thenReturn(mockResponse);

        String latitude = "40.7128";
        String longitude = "-74.0060";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/weather")
                .param("latitude", latitude)
                .param("longitude", longitude)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        verify(restTemplateMock, times(1)).getForObject(anyString(), any());
        assertEquals(mvcResult.getResponse().getStatus(), 200);
        assertEquals(mockResponse, mvcResult.getResponse().getContentAsString());

    }
}
