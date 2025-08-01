package team8.comp47360_team8_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import team8.comp47360_team8_backend.controller.POIController;
import team8.comp47360_team8_backend.controller.POITypeController;
import team8.comp47360_team8_backend.controller.UserController;
import team8.comp47360_team8_backend.controller.UserPlanController;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.model.UserPlan;
import team8.comp47360_team8_backend.security.CustomUserDetails;

import java.net.URI;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Author : Ze Li
 * @Date : 09/07/2025 01:18
 * @Version : V1.0
 * @Description :
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CSRFTest {
    // FULL TEST, need database started
    @MockitoBean
    private UserController userController;
    @MockitoBean
    private UserPlanController userPlanController;
    @MockitoBean
    private POIController poiController;
    @MockitoBean
    private POITypeController poiTypeController;
    @Autowired
    private MockMvc mockMvc;
    private static final ObjectMapper om = new ObjectMapper();

    @Test
    void csrfPassTest() throws Exception {
        // get public profile picture
        when(userController.getUserPicture("123")).thenReturn(ResponseEntity.ok(null));
        mockMvc.perform(get("/user/picture/123"))
                .andExpect(status().isOk());

        // get list of recommendations by post
        when(poiController.getListOfRecommendations(any())).thenReturn(ResponseEntity.ok(Collections.emptyList()));
        mockMvc.perform(post("/pois/recommendation").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(Collections.emptyList())))
                .andExpect(status().isOk());

        // get poitypes
        when(poiTypeController.getAllPOITypes()).thenReturn(ResponseEntity.ok(Collections.emptyList()));
        mockMvc.perform(get("/poitypes")).andExpect(status().isOk());
    }

    @Test
    void csrfForbiddenTest() throws Exception {
        CustomUserDetails customUserDetails = new CustomUserDetails("user-tester", "password", 1L, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        // get user info without csrf
        mockMvc.perform(get("/user").with(user(customUserDetails))).andExpect(status().isForbidden());
        // get user info with csrf
        when(userController.getUser()).thenReturn(ResponseEntity.ok(new User()));
        mockMvc.perform(get("/user").with(user(customUserDetails)).with(csrf()))
                .andExpect(status().isOk());

        // logout without csrf
        mockMvc.perform(post("/logout").with(user(customUserDetails)))
                .andExpect(status().isForbidden());
        // logout with csrf
        mockMvc.perform(post("/logout").with(user(customUserDetails)).with(csrf()))
                .andExpect(status().isOk());

        when(userPlanController.getUserPlans()).thenReturn(ResponseEntity.ok(Collections.emptyList()));
        // get user plans without csrf
        mockMvc.perform(get("/userplans").with(user(customUserDetails))).andExpect(status().isForbidden());
        // get user plans with csrf
        mockMvc.perform(get("/userplans").with(user(customUserDetails)).with(csrf()))
                .andExpect(status().isOk());

        when(userPlanController.createUserPlan(any(UserPlan.class))).thenReturn(ResponseEntity.created(new URI("/userplans/1")).body(new UserPlan()));
        // post user plan without csrf
        mockMvc.perform(post("/userplans").with(user(customUserDetails))).andExpect(status().isForbidden());
        // post user plan with csrf
        mockMvc.perform(post("/userplans").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(new UserPlan()))
                .with(user(customUserDetails)).with(csrf()))
                .andExpect(status().isCreated());

        when(userController.deleteUser(any(), any())).thenReturn(ResponseEntity.noContent().build());
        // delete user without csrf
        mockMvc.perform(delete("/user").with(user(customUserDetails))).andExpect(status().isForbidden());
        // delete user with csrf
        mockMvc.perform(delete("/user").with(user(customUserDetails)).with(csrf()))
                .andExpect(status().isNoContent());
    }
}
