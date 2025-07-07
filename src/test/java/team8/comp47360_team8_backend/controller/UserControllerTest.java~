package team8.comp47360_team8_backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.service.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();

        // Use reflection if no setters exist
        // OR just skip this if you're not using those values in assertions
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateUserPicture() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy data".getBytes());

        when(userService.updateUserPicture(any())).thenReturn("test.jpg");

        mockMvc.perform(multipart("/user/picture").file(file))
                .andExpect(status().isCreated());
    }

    @Test
    void getUserPicture() throws Exception {
        Resource picture = new ByteArrayResource("dummy image".getBytes()) {
            @Override
            public String getFilename() {
                return "test.jpg";
            }
        };

        when(userService.getUserPicture("test.jpg")).thenReturn(picture);

        mockMvc.perform(get("/user/picture/test.jpg"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"updateduser\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUser()).thenReturn(mockUser);

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser();

        mockMvc.perform(delete("/user"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCsrfToken() throws Exception {
        mockMvc.perform(get("/csrf-token"))
                .andExpect(status().isOk());
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeHttpRequests((authz) -> authz.anyRequest().permitAll());
            return http.build();
        }
    }
}
