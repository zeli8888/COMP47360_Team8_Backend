package team8.comp47360_team8_backend.service;

import team8.comp47360_team8_backend.dto.ZoneBusynessDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 03/06/2025 11:52
 * @Version : V1.0
 * @Description :
 */
public interface ZoneService {
    List<ZoneBusynessDTO> predictZoneBusyness(LocalDateTime dateTime);
}
