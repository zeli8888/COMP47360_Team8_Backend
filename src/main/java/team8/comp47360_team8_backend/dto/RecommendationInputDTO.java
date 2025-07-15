package team8.comp47360_team8_backend.dto;

import java.time.ZonedDateTime;

/**
 * @Author : Ze Li
 * @Date : 26/06/2025 22:17
 * @Version : V1.0
 * @Description :
 */
public class RecommendationInputDTO {
    private String poiName;
    private Long zoneId;
    private Double latitude;
    private Double longitude;
    private ZonedDateTime time;
    private String transitType;
    private String poiTypeName;
    private Integer stayMinutes;

    public RecommendationInputDTO() {
    }

    public RecommendationInputDTO(String poiName, Long zoneId, Double latitude, Double longitude, ZonedDateTime time, String transitType, String poiTypeName, Integer StayMinutes) {
        this.poiName = poiName;
        this.zoneId = zoneId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.transitType = transitType;
        this.poiTypeName = poiTypeName;
        this.stayMinutes = StayMinutes;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public String getTransitType() {
        return transitType;
    }

    public void setTransitType(String transitType) {
        this.transitType = transitType;
    }

    public String getPoiTypeName() {
        return poiTypeName;
    }

    public void setPoiTypeName(String poiTypeName) {
        this.poiTypeName = poiTypeName;
    }

    public void setStayMinutes(Integer stayMinutes) {
        this.stayMinutes = stayMinutes;
    }

    public Integer getStayMinutes() {
        return stayMinutes;
    }
}
