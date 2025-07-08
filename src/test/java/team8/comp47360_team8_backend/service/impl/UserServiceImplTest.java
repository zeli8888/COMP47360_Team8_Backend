package team8.comp47360_team8_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.repository.UserRepository;
import team8.comp47360_team8_backend.security.CustomUserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * @Author : Ze Li
 * @Date : 04/07/2025 16:22
 * @Version : V1.0
 * @Description :
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    @Spy
    private UserServiceImpl userService;
    private User testUser;

    private MockedStatic<SecurityContextHolder> mockSecurityContext() {
        MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(
                "user",
                "password",
                1L,
                Collections.emptySet()
        );

        securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        return securityContextHolder;
    }

    private MockedStatic<SecurityContextHolder> mockSecurityContextWithoutLogin() {
        MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        return securityContextHolder;
    }

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "password", "gmail@.com", "tester", "test.jpg");
    }

    @Test
    void loadUserByUsername() {
        when(userRepository.findByUserName("tester")).thenReturn(Optional.of(testUser));
        UserDetails result = userService.loadUserByUsername("tester");
        assertEquals("tester", result.getUsername());
        assertEquals("password", result.getPassword());

        when(userRepository.findByUserName("tester")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("tester")).thenReturn(Optional.of(testUser));
        result = userService.loadUserByUsername("tester");
        assertEquals("tester", result.getUsername());
        assertEquals("password", result.getPassword());

        testUser.setPassword(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("tester"));

        when(userRepository.findByEmail("tester")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("tester"));

        testUser.setUserName(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("tester"));

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(null));
    }

    @Test
    void loadUser() {
        DefaultOAuth2UserService delegate = mock(DefaultOAuth2UserService.class);
        ReflectionTestUtils.setField(userService, "delegate", delegate);
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(delegate.loadUser(any(OAuth2UserRequest.class))).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("sub")).thenReturn("new_google_id");
        when(oAuth2User.getAttribute("email")).thenReturn("new@google.com");
        when(oAuth2User.getAttribute("picture")).thenReturn("new_picture_url");
        when(oAuth2User.getAttribute("given_name")).thenReturn("new_first_name");
        when(oAuth2User.getAttribute("name")).thenReturn("new_name");

        // User with google id not exist
        when(userRepository.findByGoogleId("new_google_id")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@google.com")).thenReturn(Optional.empty());
        doReturn(testUser).when(userService).createUser(any(User.class));
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContextWithoutLogin()){
            CustomUserDetails result = (CustomUserDetails) userService.loadUser(mock(OAuth2UserRequest.class));
            verify(userService, times(1)).createUser(any(User.class));
            assertEquals(1L, result.getUserId());
        }

        // User with google id not exist, but email is registered, rare case
        when(userRepository.findByEmail("new@google.com")).thenReturn(Optional.of(testUser));
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContextWithoutLogin()){
            CustomUserDetails result = (CustomUserDetails) userService.loadUser(mock(OAuth2UserRequest.class));
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userService, times(2)).createUser(userCaptor.capture());
            assertNull(userCaptor.getValue().getEmail());
            assertEquals(1L, result.getUserId());
        }

        // User with google id not exist, already logged in
        testUser.setEmail("local@google.com");
        when(userRepository.findByGoogleId("new_google_id")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("new@google.com")).thenReturn(Optional.empty());
        doNothing().when(userService).deleteUserPicture(anyString());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            CustomUserDetails result = (CustomUserDetails) userService.loadUser(mock(OAuth2UserRequest.class));
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository, times(1)).save(userCaptor.capture());
            assertEquals("new@google.com", userCaptor.getValue().getEmail());
            assertEquals(1L, result.getUserId());
        }

        // User with google id not exist, already logged in, but already linked with google account
        testUser.setGoogleId("new_google_id");
        when(userRepository.findByEmail("new@google.com")).thenReturn(Optional.empty());
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            CustomUserDetails result = (CustomUserDetails) userService.loadUser(mock(OAuth2UserRequest.class));
            assertEquals(1L, result.getUserId());
        }

        // authenticated user not found, rare case
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            assertThrows(UsernameNotFoundException.class, () -> userService.loadUser(mock(OAuth2UserRequest.class)));
        }

        // google id null
        when(oAuth2User.getAttribute("sub")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUser(mock(OAuth2UserRequest.class)));

    }

    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateUserPicture() {
    }

    @Test
    void getUserPicture() {
    }
}