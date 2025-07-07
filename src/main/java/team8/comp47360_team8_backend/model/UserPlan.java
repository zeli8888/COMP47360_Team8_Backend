package team8.comp47360_team8_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 17:06
 * @Version : V1.0
 * @Description :
 */

@Entity
public class UserPlan {
    @Id
    @GeneratedValue
    private Long userPlanId;

    private String poiName;

    @Column(nullable = false)
    private ZonedDateTime time;

    private String busyness;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private float userRating;

    private String pictureUrl;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JsonIgnore
    private User user;

    public UserPlan() {
    }

    public UserPlan(String poiName, ZonedDateTime time, String busyness, double latitude, double longitude) {
        this.poiName = poiName;
        this.time = time;
        this.busyness = busyness;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UserPlan(Long userPlanId, String poiName, ZonedDateTime time, String busyness, double latitude,
            double longitude, User user) {
        this.userPlanId = userPlanId;
        this.poiName = poiName;
        this.time = time;
        this.busyness = busyness;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }

    public UserPlan(Long userPlanId, String poiName, ZonedDateTime time, String busyness, double latitude, double longitude, float userRating, String pictureUrl, User user) {
        this.userPlanId = userPlanId;
        this.poiName = poiName;
        this.time = time;
        this.busyness = busyness;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userRating = userRating;
        this.pictureUrl = pictureUrl;
        this.user = user;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Long getUserPlanId() {
        return userPlanId;
    }

    public void setUserPlanId(Long userPlanId) {
        this.userPlanId = userPlanId;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public String getBusyness() {
        return busyness;
    }

    public void setBusyness(String busyness) {
        this.busyness = busyness;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
