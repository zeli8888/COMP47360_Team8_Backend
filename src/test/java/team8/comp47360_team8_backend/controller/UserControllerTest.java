package team8.comp47360_team8_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // disables Spring Security filters for tests
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private User mockUser = new User();

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
        // mock static Paths and Files
        Resource resource = mock(Resource.class);
        when(userService.getUserPicture(anyString())).thenReturn(resource);
        File file = mock(File.class);
        when(resource.getFile()).thenReturn(file);
        Path path = mock(Path.class);
        when(file.toPath()).thenReturn(path);

        // probeContentType successfully
        try (MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(()->Files.probeContentType(path)).thenReturn("image/jpeg");
            mockMvc.perform(get("/user/picture/test.jpg"))
                    .andExpect(status().isOk());
        }

        // IOException while probing content type
        try (MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            filesMockedStatic.when(()->Files.probeContentType(path)).thenThrow(IOException.class);
            mockMvc.perform(get("/user/picture/test.jpg"))
                    .andExpect(status().isOk());
        }
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

}
