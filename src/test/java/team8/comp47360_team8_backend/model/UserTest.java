package team8.comp47360_team8_backend.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testDefaultConstructorAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setGoogleId("google-123");
        user.setPassword("pass123");
        user.setEmail("user@example.com");
        user.setUserName("testuser");
        user.setUserPicture("profile.jpg");

        assertEquals(1L, user.getId());
        assertEquals("google-123", user.getGoogleId());
        assertEquals("pass123", user.getPassword());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("testuser", user.getUserName());
        assertEquals("profile.jpg", user.getUserPicture());
    }

    @Test
    void testParameterizedConstructorWithoutUserPlans() {
        User user = new User(2L, "pass456", "user2@example.com", "user2", "pic2.jpg");

        assertEquals(2L, user.getId());
        assertEquals("pass456", user.getPassword());
        assertEquals("user2@example.com", user.getEmail());
        assertEquals("user2", user.getUserName());
        assertEquals("pic2.jpg", user.getUserPicture());
    }

    @Test
    void testParameterizedConstructorWithUserPlans() {
        Set<UserPlan> plans = new HashSet<>();
        plans.add(new UserPlan("POI A", java.time.ZonedDateTime.now(), "Medium", 10.0, 20.0));

        User user = new User(3L, "google-456", "secret", "user3@example.com", "user3", plans, "pic3.jpg");

        assertEquals(3L, user.getId());
        assertEquals("google-456", user.getGoogleId());
        assertEquals("secret", user.getPassword());
        assertEquals("user3@example.com", user.getEmail());
        assertEquals("user3", user.getUserName());
        assertEquals("pic3.jpg", user.getUserPicture());
        assertEquals(1, user.getUserPlans().size());
    }

    @Test
    void testSetAndGetUserPlans() {
        User user = new User();
        Set<UserPlan> planSet = new HashSet<>();
        planSet.add(new UserPlan("POI B", java.time.ZonedDateTime.now(), "Low", 30.0, 40.0));

        user.setUserPlans(planSet);
        assertEquals(1, user.getUserPlans().size());
    }
}
