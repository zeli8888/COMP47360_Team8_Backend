package team8.comp47360_team8_backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 20:28
 * @Version : V1.0
 * @Description :
 */
@Entity
public class Zone {
    @Id
    @Column(name = "zone_id")
    private long zoneId;

    @Column(unique = true, nullable = false)
    @JsonIgnore
    private String zoneName;

    public Zone() {
    }

    public Zone(long zoneId, String zoneName) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
    }

    public long getZoneId() {
        return zoneId;
    }

    public void setZoneId(long zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

}
