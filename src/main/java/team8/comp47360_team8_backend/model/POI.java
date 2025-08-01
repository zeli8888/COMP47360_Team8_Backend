package team8.comp47360_team8_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 20:45
 * @Version : V1.0
 * @Description :
 */

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class POI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long poiId;

    @Column(nullable = false)
    private String poiName;

    private String poiDescription;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = true)
    private Float userRating;

    private String pictureUrl;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "zone_id", nullable = false, referencedColumnName = "zone_id")
    private Zone zone;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "poi_type_id", nullable = false, referencedColumnName = "poi_type_id")
    @JsonIgnore
    private POIType poiType;

    public POI() {
    }

    public POI(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public POI(Long poiId, String poiName, String poiDescription, double latitude, double longitude, Zone zone, POIType poiType) {
        this.poiId = poiId;
        this.poiName = poiName;
        this.poiDescription = poiDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zone = zone;
        this.poiType = poiType;
    }

    public POI(Long poiId, String poiName, String poiDescription, double latitude, double longitude, float userRating, String pictureUrl, Zone zone, POIType poiType) {
        this.poiId = poiId;
        this.poiName = poiName;
        this.poiDescription = poiDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userRating = userRating;
        this.pictureUrl = pictureUrl;
        this.zone = zone;
        this.poiType = poiType;
    }

    public Float getUserRating() {
        return userRating;
    }

    public void setUserRating(Float userRating) {
        this.userRating = userRating;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getPoiDescription() {
        return poiDescription;
    }

    public void setPoiDescription(String poiDescription) {
        this.poiDescription = poiDescription;
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

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public POIType getPoiType() {
        return poiType;
    }

    public void setPoiType(POIType poiType) {
        this.poiType = poiType;
    }

    public Long getPoiId() {
        return poiId;
    }

    public void setPoiId(Long poiId) {
        this.poiId = poiId;
    }

}
