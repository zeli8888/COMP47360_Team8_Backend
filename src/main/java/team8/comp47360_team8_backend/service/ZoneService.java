package team8.comp47360_team8_backend.service;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 03/06/2025 11:52
 * @Version : V1.0
 * @Description :
 */
public interface ZoneService {
    HashMap<Long, String> predictZoneBusyness(ZonedDateTime dateTime);
    List<String> predictZoneBusyness(List<ZonedDateTime> dateTimeList, Long zoneId);
}
