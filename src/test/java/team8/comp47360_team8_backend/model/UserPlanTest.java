package team8.comp47360_team8_backend.model;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserPlanTest {

    @Test
    void testDefaultConstructorAndSetters() {
        UserPlan plan = new UserPlan();
        User user = new User();
        ZonedDateTime now = ZonedDateTime.now();

        plan.setUserPlanId(1L);
        plan.setPoiName("Test POI");
        plan.setTime(now);
        plan.setBusyness("High");
        plan.setLatitude(12.345);
        plan.setLongitude(67.89);
        plan.setUser(user);

        assertEquals(1L, plan.getUserPlanId());
        assertEquals("Test POI", plan.getPoiName());
        assertEquals(now, plan.getTime());
        assertEquals("High", plan.getBusyness());
        assertEquals(12.345, plan.getLatitude());
        assertEquals(67.89, plan.getLongitude());
        assertEquals(user, plan.getUser());
    }

    @Test
    void testMinimalConstructor() {
        ZonedDateTime now = ZonedDateTime.now();
        UserPlan plan = new UserPlan("POI Minimal", now, "Low", 1.1, 2.2);

        assertNull(plan.getUserPlanId());
        assertEquals("POI Minimal", plan.getPoiName());
        assertEquals(now, plan.getTime());
        assertEquals("Low", plan.getBusyness());
        assertEquals(1.1, plan.getLatitude());
        assertEquals(2.2, plan.getLongitude());
        assertNull(plan.getUser()); // user not set
    }

    @Test
    void testFullConstructor() {
        ZonedDateTime now = ZonedDateTime.now();
        User user = new User();
        UserPlan plan = new UserPlan(99L, "POI Full", now, "Medium", 3.3, 4.4, user);

        assertEquals(99L, plan.getUserPlanId());
        assertEquals("POI Full", plan.getPoiName());
        assertEquals(now, plan.getTime());
        assertEquals("Medium", plan.getBusyness());
        assertEquals(3.3, plan.getLatitude());
        assertEquals(4.4, plan.getLongitude());
        assertEquals(user, plan.getUser());
    }
}
