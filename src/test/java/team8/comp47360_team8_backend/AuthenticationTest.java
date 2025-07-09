package team8.comp47360_team8_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.HashMap;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * @Author : Ze Li
 * @Date : 08/07/2025 23:27
 * @Version : V1.0
 * @Description :
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationTest {
    // FULL TEST, need database started
    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper om = new ObjectMapper();

    @Test
    void authenticationTest() throws Exception {
        // register
        HashMap<String, String> input = new HashMap<>();
        input.put("userName", "tester");
        input.put("password", "password");
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(input)))
                .andExpect(status().isCreated());

        // login
        mockMvc.perform(post("/login").contentType(MediaType.MULTIPART_FORM_DATA).param("username", "tester").param("password", "password"))
                .andExpect(status().isOk());

        // failed login
        mockMvc.perform(post("/login").contentType(MediaType.MULTIPART_FORM_DATA).param("username", "tester").param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }

}