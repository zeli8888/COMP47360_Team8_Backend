package team8.comp47360_team8_backend.dto;

import team8.comp47360_team8_backend.model.POI;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 03/06/2025 11:46
 * @Version : V1.0
 * @Description :
 */
public class POIZoneBusynessDTO {
    private List<POI> pois;
    private List<ZoneBusynessDTO> zoneBusynesses;

    public POIZoneBusynessDTO() {
    }

    public POIZoneBusynessDTO(List<POI> pois, List<ZoneBusynessDTO> zoneBusynesses) {
        this.pois = pois;
        this.zoneBusynesses = zoneBusynesses;
    }

    public List<POI> getPois() {
        return pois;
    }

    public void setPois(List<POI> pois) {
        this.pois = pois;
    }

    public List<ZoneBusynessDTO> getZoneBusynesses() {
        return zoneBusynesses;
    }

    public void setZoneBusynesses(List<ZoneBusynessDTO> zoneBusynesses) {
        this.zoneBusynesses = zoneBusynesses;
    }
}
