package team8.comp47360_team8_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import team8.comp47360_team8_backend.exception.EmailAlreadyExistException;
import team8.comp47360_team8_backend.exception.UserAlreadyExistException;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.repository.UserRepository;
import team8.comp47360_team8_backend.security.CustomUserDetails;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
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
        testUser = new User(1L, "password", "gmail@.com", "tester", "user_1_test.jpg");
        // Set upload path
        ReflectionTestUtils.setField(userService, "uploadPath", "/uploads");

        // Set picture size (1024KB = 1MB)
        ReflectionTestUtils.setField(userService, "userPictureSize", 5242880);
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
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            when(userRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(UsernameNotFoundException.class, () -> userService.loadUser(mock(OAuth2UserRequest.class)));
        }

        // google id null
        when(oAuth2User.getAttribute("sub")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUser(mock(OAuth2UserRequest.class)));

    }

    @Test
    void createUser() {
        doNothing().when(userService).validateEmail(anyString());
        doNothing().when(userService).validateUsername(anyString());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // create user without google id
        User result = userService.createUser(testUser);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        assertEquals("encoded_password", userCaptor.getValue().getPassword());
        assertNull(result.getGoogleId());
        assertNull(result.getPassword());
        assertNull(result.getEmail());
        assertNull(result.getUserPicture());

        // create user with google id
        setUp();
        testUser.setGoogleId("google_id");
        result = userService.createUser(testUser);
        userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(userCaptor.capture());
        assertNull(userCaptor.getValue().getPassword());
        assertEquals("google_id", userCaptor.getValue().getGoogleId());
        assertNull(result.getGoogleId());
        assertNull(result.getPassword());
        assertEquals("gmail@.com", result.getEmail());
        assertEquals("user_1_test.jpg", result.getUserPicture());
        assertNull(result.getUserName());

        // user without google id and username
        setUp();
        testUser.setUserName(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.createUser(testUser));

        // user without google id and password
        setUp();
        testUser.setPassword(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.createUser(testUser));

    }

    @Test
    void updateUser() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            // normal update userName and password
            User newUser = new User(testUser.getId(), "new_password", "new_"+testUser.getEmail(), "new_"+testUser.getUserName(), testUser.getUserPicture());
            doNothing().when(userService).validateUsername(anyString());
            when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
            when(userRepository.save(any(User.class))).thenReturn(newUser);
            doReturn(newUser).when(userService).getUserWithoutIdPassword(any(User.class));
            User result = userService.updateUser(newUser);
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository, times(1)).save(userCaptor.capture());
            assertEquals("encoded_password", userCaptor.getValue().getPassword());
            assertEquals("new_tester", userCaptor.getValue().getUserName());

            // nothing update
            newUser.setUserName(null);
            newUser.setPassword(null);
            result = userService.updateUser(newUser);
            verify(userRepository, times(2)).save(userCaptor.capture());
            assertEquals("encoded_password", userCaptor.getValue().getPassword());
            assertEquals("new_tester", userCaptor.getValue().getUserName());

            // userName unchanged
            setUp();
            newUser.setUserName(testUser.getUserName());
            result = userService.updateUser(newUser);
            verify(userRepository, times(3)).save(userCaptor.capture());
            assertEquals("encoded_password", userCaptor.getValue().getPassword());
            assertEquals("tester", userCaptor.getValue().getUserName());
        }
    }

    @Test
    void getUser() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            doReturn(testUser).when(userService).getUserWithoutIdPassword(any(User.class));
            User result = userService.getUser();
            assertEquals(testUser, result);
        }

        // authenticated user not found
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            when(userRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(UsernameNotFoundException.class, () -> userService.getUser());
        }
    }

    @Test
    void deleteUser() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()) {
            doNothing().when(userService).deleteUserPicture(anyString());
            doNothing().when(userRepository).delete(any(User.class));
            userService.deleteUser();
            verify(userService, times(1)).deleteUserPicture(anyString());
            verify(userRepository, times(1)).delete(any(User.class));
        }
    }

    @Test
    void updateUserPicture() {
        // Create a test file
        MultipartFile file = new MockMultipartFile("file", "new_test.jpg", "image/jpeg", "data".getBytes());

        // Mock the Paths and Files classes
        Path path;
        try (MockedStatic<Paths> paths = mockStatic(Paths.class)) {
            path = mock(Path.class);
            paths.when(() -> Paths.get(anyString())).thenReturn(path);
        }
        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            files.when(() -> Files.exists(any(Path.class))).thenReturn(false);
            files.when(() -> Files.createDirectories(any(Path.class))).thenReturn(path);
            when(path.resolve(anyString())).thenReturn(path);
            files.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class))).thenReturn(1L);

            // normal update user picture
            doNothing().when(userService).deleteUserPicture(anyString());
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()) {
                String result = userService.updateUserPicture(file);
                assertEquals("user_1_new_test.jpg", result);
                verify(userService, times(1)).deleteUserPicture(anyString());
                verify(userRepository, times(1)).save(any(User.class));
                files.verify(() -> Files.createDirectories(any(Path.class)), times(1));
                files.verify(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)), times(1));
            }

            // normal update user picture with uploadPath existed and user picture same name
            files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
            setUp();
            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()) {
                String result = userService.updateUserPicture(file);
                assertEquals("user_1_test.jpg", result);
                verify(userService, times(1)).deleteUserPicture(anyString());
                verify(userRepository, times(2)).save(any(User.class));
                files.verify(() -> Files.createDirectories(any(Path.class)), times(1));
                files.verify(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)), times(2));
            }

            // file is empty or has no name
            MultipartFile fileNoName = new MockMultipartFile("file", null, "image/jpeg", "data".getBytes());
            ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> userService.updateUserPicture(fileNoName));
            assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
            MultipartFile fileEmpty = new MockMultipartFile("file", "test.jpg", "image/jpeg", "".getBytes());
            ResponseStatusException responseStatusException2 = assertThrows(ResponseStatusException.class, () -> userService.updateUserPicture(fileEmpty));
            assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());

            // file size too large
            MultipartFile fileTooLarge = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[5242881]);
            ResponseStatusException responseStatusException3 = assertThrows(ResponseStatusException.class, () -> userService.updateUserPicture(fileTooLarge));
            assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());

            // file content type not allowed
            MultipartFile fileNotAllowed = new MockMultipartFile("file", "test.jpg", "text/plain", "data".getBytes());
            ResponseStatusException responseStatusException4 = assertThrows(ResponseStatusException.class, () -> userService.updateUserPicture(fileNotAllowed));
            assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());

            // file save failed
            setUp();
            MultipartFile fileFailed = new MockMultipartFile("file", "new_test.jpg", "image/jpeg", "data".getBytes());
            try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()) {
                files.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class))).thenThrow(IOException.class);
                ResponseStatusException responseStatusException5 = assertThrows(ResponseStatusException.class, () -> userService.updateUserPicture(fileFailed));
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatusException5.getStatusCode());
            }
        }
    }

    @Test
    void getUserPicture() throws MalformedURLException {
        // mock static Paths and Files
        Path path;
        try (MockedStatic<Paths> paths = mockStatic(Paths.class)) {
            path = mock(Path.class);
            paths.when(() -> Paths.get(anyString())).thenReturn(path);
        }
        when(path.resolve(anyString())).thenReturn(path);

        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            // normal get user picture
            files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            files.when(() -> Files.isReadable(any(Path.class))).thenReturn(true);

            URI uri = mock(URI.class);
            when(path.toUri()).thenReturn(uri);
            try (MockedConstruction<UrlResource> urlResourceMockedConstruction =
                         mockConstruction(UrlResource.class, (mock, context) -> {
                             when(mock.exists()).thenReturn(true);
                         })
                ) {
                // normal get user picture
                Resource userPicture = userService.getUserPicture("user_1_test.jpg");
                files.verify(() -> Files.exists(any(Path.class)), times(1));
                files.verify(() -> Files.isReadable(any(Path.class)), times(1));
                UrlResource resource = urlResourceMockedConstruction.constructed().get(0);
                verify(resource, times(1)).exists();
            }

            // user picture not found or not readable
            files.when(() -> Files.exists(any(Path.class))).thenReturn(false);
            ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> userService.getUserPicture("user_1_test.jpg"));
            assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
            files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            files.when(() -> Files.isReadable(any(Path.class))).thenReturn(false);
            ResponseStatusException responseStatusException2 = assertThrows(ResponseStatusException.class, () -> userService.getUserPicture("user_1_test.jpg"));
            assertEquals(HttpStatus.NOT_FOUND, responseStatusException2.getStatusCode());

            // resource not exist
            files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            files.when(() -> Files.isReadable(any(Path.class))).thenReturn(true);
            try (MockedConstruction<UrlResource> urlResourceMockedConstruction =
                         mockConstruction(UrlResource.class, (mock, context) -> {
                             when(mock.exists()).thenReturn(false);
                         })
                ) {
                ResponseStatusException responseStatusException3 = assertThrows(ResponseStatusException.class, () -> userService.getUserPicture("user_1_test.jpg"));
                assertEquals(HttpStatus.NOT_FOUND, responseStatusException3.getStatusCode());
            }

            // resource creation failed
            try (MockedConstruction<UrlResource> ignored = mockConstructionWithAnswer(UrlResource.class, invocation -> {
                throw new MalformedURLException();
            })) {
                ResponseStatusException responseStatusException4 = assertThrows(
                        ResponseStatusException.class,
                        () -> userService.getUserPicture("user_1_test.jpg")
                );
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatusException4.getStatusCode());
            }

        }
    }

    @Test
    void deleteUserPicture() {
        // mock static Paths and Files
        Path path = mock(Path.class);
        when(path.resolve(anyString())).thenReturn(path);
        try (MockedStatic<Paths> paths = mockStatic(Paths.class)) {
            paths.when(() -> Paths.get(anyString())).thenReturn(path);
            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                // normal deletion
                files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
                files.when(() -> Files.delete(any(Path.class))).then(invocation -> null);
                userService.deleteUserPicture("user_1_test.jpg");
                files.verify(() -> Files.exists(any(Path.class)), times(1));
                files.verify(() -> Files.delete(any(Path.class)), times(1));
            }

            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                // file not exist
                files.when(() -> Files.exists(any(Path.class))).thenReturn(false);
                userService.deleteUserPicture("user_1_test.jpg");
                files.verify(() -> Files.exists(any(Path.class)), times(1));
                files.verify(() -> Files.delete(any(Path.class)), times(0));
            }

            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                // deletion failed
                files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
                files.when(() -> Files.delete(any(Path.class))).thenThrow(IOException.class);
                ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.deleteUserPicture("user_1_test.jpg"));
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
                files.verify(() -> Files.exists(any(Path.class)), times(1));
                files.verify(() -> Files.delete(any(Path.class)), times(1));
            }
        }

        try (MockedStatic<Paths> paths = mockStatic(Paths.class)) {
            // delete user picture with null
            userService.deleteUserPicture(null);
            paths.verify(() -> Paths.get(anyString()), times(0));
        }

        try (MockedStatic<Paths> paths = mockStatic(Paths.class)) {
            // delete user picture start with http
            userService.deleteUserPicture("http://example.com/image.jpg");
            paths.verify(() -> Paths.get(anyString()), times(0));
        }
    }

    @Test
    void getUserWithoutIdPassword() {
        // user with local picture url
        try (MockedStatic<ServletUriComponentsBuilder> servletUriComponentsBuilderMockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder servletUriComponentsBuilder = mock(ServletUriComponentsBuilder.class);
            servletUriComponentsBuilderMockedStatic.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(servletUriComponentsBuilder);
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080/api/user/picture/{pictureUrl}");
            when(servletUriComponentsBuilder.path(anyString())).thenReturn(uriComponentsBuilder);
            User user = userService.getUserWithoutIdPassword(testUser);
            assertNull(user.getId());
            assertNull(user.getPassword());
            assertEquals("http://localhost:8080/api/user/picture/user_1_test.jpg", user.getUserPicture());
        }

        // user with google picture url
        testUser.setUserPicture("http://example.com/image.jpg");
        User user = userService.getUserWithoutIdPassword(testUser);
        assertNull(user.getId());
        assertNull(user.getPassword());
        assertEquals("http://example.com/image.jpg", user.getUserPicture());

        // user without picture url
        testUser.setUserPicture(null);
        user = userService.getUserWithoutIdPassword(testUser);
        assertNull(user.getId());
        assertNull(user.getPassword());
        assertNull(user.getUserPicture());
    }

    @Test
    void validateEmail() {
        // valid email
        String email = "valid_email@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> userService.validateEmail(email));
        verify(userRepository, times(1)).findByEmail(email);

        // existing email
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));
        EmailAlreadyExistException exception = assertThrows(EmailAlreadyExistException.class,
                () -> userService.validateEmail(email));
        verify(userRepository, times(2)).findByEmail(email);

        // invalid email
        String invalidEmail = "invalid_email";
        UsernameNotFoundException exception2 = assertThrows(UsernameNotFoundException.class,
                () -> userService.validateEmail(invalidEmail));
        assertEquals("User email invalid: " + invalidEmail, exception2.getMessage());
    }

    @Test
    void validateUsername() {
        // valid username
        String username = "valid_user-name";
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> userService.validateUsername(username));

        // existing username
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(new User()));
        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class,
                () -> userService.validateUsername(username));

        // invalid username
        String invalidUsername = "invalid username";
        UsernameNotFoundException exception2 = assertThrows(UsernameNotFoundException.class,
                () -> userService.validateUsername(invalidUsername));
        assertEquals("User name invalid: " + invalidUsername, exception2.getMessage());
    }
}