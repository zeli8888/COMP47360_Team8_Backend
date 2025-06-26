package team8.comp47360_team8_backend.dto;

import java.util.HashMap;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 26/06/2025 17:02
 * @Version : V1.0
 * @Description :
 */
public class POIZoneBusynessDTO {
    private List<POIBusynessDistanceRecommendationDTO> busynessDistanceRecommendationDTOS;
    private HashMap<Long, String> zoneBusynessMap;

    public POIZoneBusynessDTO() {
    }

    public POIZoneBusynessDTO(List<POIBusynessDistanceRecommendationDTO> busynessDistanceRecommendationDTOS, HashMap<Long, String> zoneBusynessMap) {
        this.busynessDistanceRecommendationDTOS = busynessDistanceRecommendationDTOS;
        this.zoneBusynessMap = zoneBusynessMap;
    }

    public List<POIBusynessDistanceRecommendationDTO> getBusynessDistanceRecommendationDTOS() {
        return busynessDistanceRecommendationDTOS;
    }

    public void setBusynessDistanceRecommendationDTOS(List<POIBusynessDistanceRecommendationDTO> busynessDistanceRecommendationDTOS) {
        this.busynessDistanceRecommendationDTOS = busynessDistanceRecommendationDTOS;
    }

    public HashMap<Long, String> getZoneBusynessMap() {
        return zoneBusynessMap;
    }

    public void setZoneBusynessMap(HashMap<Long, String> zoneBusynessMap) {
        this.zoneBusynessMap = zoneBusynessMap;
    }
}
