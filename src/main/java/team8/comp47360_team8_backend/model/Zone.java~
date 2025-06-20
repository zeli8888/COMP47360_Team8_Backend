package team8.comp47360_team8_backend.model;
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
    private String zoneName;

    private Long numPOI;

    public Zone() {
    }

    public Zone(long zoneId, String zoneName, Long numPOI) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.numPOI = numPOI;
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

    public Long getNumPOI() {
        return numPOI;
    }

    public void setNumPOI(Long numPOI) {
        this.numPOI = numPOI;
    }
}
