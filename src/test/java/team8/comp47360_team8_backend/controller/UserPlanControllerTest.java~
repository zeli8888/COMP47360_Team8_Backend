package team8.comp47360_team8_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.model.UserPlan;
import team8.comp47360_team8_backend.service.UserPlanService;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPlanController.class)
@AutoConfigureMockMvc(addFilters = false) // disables Spring Security filters for tests
class UserPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserPlanService userPlanService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // ✅ Handles ZonedDateTime
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void getUserPlans() throws Exception {
        UserPlan userPlan = new UserPlan(1L, "Test POI", ZonedDateTime.now(), "Low", 10.0, 20.0, new User());
        Mockito.when(userPlanService.getUserPlans()).thenReturn(Collections.singletonList(userPlan));

        mockMvc.perform(get("/userplans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].poiName").value("Test POI"));
    }

    @Test
    void createUserPlan() throws Exception {
        UserPlan userPlan = new UserPlan(null, "Create Test", ZonedDateTime.now(), "Medium", 11.0, 21.0, new User());
        Mockito.when(userPlanService.createUserPlan(Mockito.any(UserPlan.class))).thenReturn(userPlan);

        mockMvc.perform(post("/userplans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPlan)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.poiName").value("Create Test"));
    }

    @Test
    void testCreateUserPlan_Multiple() throws Exception {
        UserPlan userPlan = new UserPlan(null, "Multiple Test", ZonedDateTime.now(), "High", 12.0, 22.0, new User());
        List<UserPlan> plans = List.of(userPlan);
        Mockito.when(userPlanService.createUserPlan(Mockito.anyList())).thenReturn(plans);

        mockMvc.perform(post("/userplans/multiple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(plans)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].poiName").value("Multiple Test"));
    }

    @Test
    void deleteUserPlan() throws Exception {
        mockMvc.perform(delete("/userplans/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUserPlan_Multiple() throws Exception {
        List<Long> ids = List.of(1L);
        mockMvc.perform(delete("/userplans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateUserPlan() throws Exception {
        UserPlan updatedPlan = new UserPlan(1L, "Updated POI", ZonedDateTime.now(), "High", 13.0, 23.0, new User());
        Mockito.when(userPlanService.updateUserPlan(Mockito.eq(1L), Mockito.any(UserPlan.class)))
                .thenReturn(updatedPlan);

        mockMvc.perform(put("/userplans/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPlan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poiName").value("Updated POI"));
    }
}
