package team8.comp47360_team8_backend.dto;

import team8.comp47360_team8_backend.model.Zone;

/**
 * @Author : Ze Li
 * @Date : 03/06/2025 11:49
 * @Version : V1.0
 * @Description :
 */
public class ZoneBusynessDTO {
    private Zone zone;
    private double busyness;

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public double getBusyness() {
        return busyness;
    }

    public void setBusyness(double busyness) {
        this.busyness = busyness;
    }

    public ZoneBusynessDTO() {
    }

    public ZoneBusynessDTO(Zone zone, double busyness) {
        this.zone = zone;
        this.busyness = busyness;
    }
}