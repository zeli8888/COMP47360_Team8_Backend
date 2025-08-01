package team8.comp47360_team8_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team8.comp47360_team8_backend.exception.UnauthorizedAccessException;
import team8.comp47360_team8_backend.exception.UserPlanNotFoundException;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.model.UserPlan;
import team8.comp47360_team8_backend.repository.UserPlanRepository;
import team8.comp47360_team8_backend.repository.UserRepository;
import team8.comp47360_team8_backend.security.CustomUserDetails;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @Author : Ze Li
 * @Date : 03/07/2025 18:33
 * @Version : V1.0
 * @Description :
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserPlanServiceImplTest {
    @Mock
    private UserPlanRepository userPlanRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPlanServiceImpl userPlanService = new UserPlanServiceImpl();

    private  CustomUserDetails expectedUserDetails;
    private User user;
    private User user2;
    private UserPlan userPlan1;
    private UserPlan userPlan2;
    private UserPlan userPlan3;
    private UserPlan userPlan4;

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

    @BeforeEach
    void getUserDetails() {
        user = new User(1L, "password", "test@gmail.com", "tester", "test.jpg");
        userPlan1 = new UserPlan(1L, "Test POI", ZonedDateTime.now(), "Low", 10.0, 20.0, user);
        userPlan2 = new UserPlan(2L, "Test POI", ZonedDateTime.now(), "Low", 10.0, 20.0, user);
        userPlan3 = new UserPlan(3L, "Test POI", ZonedDateTime.now(), "Low", 10.0, 20.0, user);
        user2 = new User(2L, "password", "test@gmail.com", "tester", "test.jpg");
        userPlan4 = new UserPlan(4L, "Test POI", ZonedDateTime.now(), "Low", 10.0, 20.0, user2);
    }

    @Test
    void getUserPlans() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            when(userPlanRepository.findByUserId(1L)).thenReturn(Collections.singletonList(userPlan1));
            List<UserPlan> userPlans = userPlanService.getUserPlans();
            assertEquals(1, userPlans.size());
            assertEquals(userPlan1, userPlans.get(0));
        }
    }

    @Test
    void deleteUserPlan() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            when(userPlanRepository.findById(1L)).thenReturn(Optional.of(userPlan1));
            when(userPlanRepository.findById(2L)).thenReturn(Optional.of(userPlan2));
            when(userPlanRepository.findById(3L)).thenReturn(Optional.of(userPlan3));
            when(userPlanRepository.findById(4L)).thenReturn(Optional.of(userPlan4));
            when(userPlanRepository.findById(5L)).thenReturn(Optional.empty());
            doNothing().when(userPlanRepository).deleteById(anyLong());
            List<Long> userPlanIds = List.of(1L, 2L, 3L);
            userPlanService.deleteUserPlan(userPlanIds);

            List<Long> userPlanIds2 = List.of(4L);
            assertThrows(UnauthorizedAccessException.class, () -> userPlanService.deleteUserPlan(userPlanIds2));

            List<Long> userPlanIds3 = List.of(5L);
            assertThrows(UserPlanNotFoundException.class, () -> userPlanService.deleteUserPlan(userPlanIds3));
        }
    }

    @Test
    void createUserPlan() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            when (userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userPlanRepository.save(any(UserPlan.class))).thenReturn(userPlan1);
            userPlanService.createUserPlan(userPlan1);
            assertEquals(user, userPlan1.getUser());
            assertNull(userPlan1.getUserPlanId());

            when (userRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> userPlanService.createUserPlan(userPlan1));
        }
    }

    @Test
    void updateUserPlan() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            when(userPlanRepository.findById(1L)).thenReturn(Optional.of(userPlan1));
            when(userPlanRepository.save(any(UserPlan.class))).thenReturn(userPlan1);
            userPlanService.updateUserPlan(1L, userPlan1);
            assertEquals(1L, userPlan1.getUserPlanId());
            assertEquals(user, userPlan1.getUser());

            when(userPlanRepository.findById(2L)).thenReturn(Optional.empty());
            assertThrows(UserPlanNotFoundException.class, () -> userPlanService.updateUserPlan(2L, userPlan1));

            when(userPlanRepository.findById(3L)).thenReturn(Optional.of(userPlan4));
            assertThrows(UnauthorizedAccessException.class, () -> userPlanService.updateUserPlan(3L, userPlan1));
        }
    }


    @Test
    void createUserPlans() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockSecurityContext()){
            when(userPlanRepository.saveAll(anyList())).thenReturn(Collections.singletonList(userPlan1));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            List<UserPlan> userPlans = userPlanService.createUserPlan(Collections.singletonList(userPlan1));
            assertEquals(1, userPlans.size());
            assertEquals(userPlan1, userPlans.get(0));

            when(userRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> userPlanService.createUserPlan(Collections.singletonList(userPlan1)));
        }
    }
}