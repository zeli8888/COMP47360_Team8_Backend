package team8.comp47360_team8_backend.service;

import team8.comp47360_team8_backend.dto.POIBusynessDistanceRecommendationDTO;
import team8.comp47360_team8_backend.dto.RecommendationInputDTO;
import team8.comp47360_team8_backend.model.POI;
import team8.comp47360_team8_backend.model.UserPlan;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 22:02
 * @Version : V1.0
 * @Description :
 */
public interface POIService {
    Set<POI> getPOIsByPOITypeName(String poiTypeName);
    List<POIBusynessDistanceRecommendationDTO> assignBusynessDistanceForPOIs(String poiTypeName, POI lastPOI, HashMap<Long, String> zoneBusynessMap, String transitType);
    List<UserPlan> getListOfRecommendations(List<RecommendationInputDTO> recommendationInputDTOS);
}
