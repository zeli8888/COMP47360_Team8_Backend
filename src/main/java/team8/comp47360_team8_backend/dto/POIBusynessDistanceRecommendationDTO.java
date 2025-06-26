package team8.comp47360_team8_backend.dto;

import team8.comp47360_team8_backend.model.POI;

/**
 * @Author : Ze Li
 * @Date : 06/06/2025 12:20
 * @Version : V1.0
 * @Description :
 */
public class POIBusynessDistanceRecommendationDTO {
    private POI poi;
    private String busyness;
    private double distance;
    private double recommendation;

    public POIBusynessDistanceRecommendationDTO() {
    }

    public POIBusynessDistanceRecommendationDTO(POI poi, String busyness, double distance, double recommendation) {
        this.poi = poi;
        this.busyness = busyness;
        this.distance = distance;
        this.recommendation = recommendation;
    }

    public double getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(double recommendation) {
        this.recommendation = recommendation;
    }

    public POI getPoi() {
        return poi;
    }

    public void setPoi(POI poi) {
        this.poi = poi;
    }

    public String getBusyness() {
        return busyness;
    }

    public void setBusyness(String busyness) {
        this.busyness = busyness;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
