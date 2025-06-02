package team8.comp47360_team8_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 20:55
 * @Version : V1.0
 * @Description :
 */

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class POIType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poi_type_id")
    private Long poiTypeID;

    @Column(unique = true, nullable = false)
    private String poiTypeName;

    @JsonIgnore
    @OneToMany(mappedBy = "poiType", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<POI> POIs = new HashSet<>();

    public POIType() {
    }

    public POIType(Long poiTypeID, String poiTypeName, Set<POI> POIs) {
        this.poiTypeID = poiTypeID;
        this.poiTypeName = poiTypeName;
        this.POIs = POIs;
    }

    public Set<POI> getPOIs() {
        return POIs;
    }

    public void setPOIs(Set<POI> POIs) {
        this.POIs = POIs;
    }

    public Long getPoiTypeID() {
        return poiTypeID;
    }

    public void setPoiTypeID(Long poiTypeID) {
        this.poiTypeID = poiTypeID;
    }

    public String getPoiTypeName() {
        return poiTypeName;
    }

    public void setPoiTypeName(String poiType) {
        this.poiTypeName = poiType;
    }
}
