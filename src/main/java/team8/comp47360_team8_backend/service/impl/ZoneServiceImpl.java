package team8.comp47360_team8_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.comp47360_team8_backend.repository.ZoneRepository;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 03/06/2025 11:56
 * @Version : V1.0
 * @Description :
 */

@Service
public class ZoneServiceImpl implements ZoneService {
    @Autowired
    private ZoneRepository zoneRepository;

    @Override
    public HashMap<Long, String> predictZoneBusyness(ZonedDateTime dateTime) {
        List<Long> zoneIds = zoneRepository.findDistinctZoneIds();

        // to integrate with the ML model, one fixed time for multiple zones
        List<String> busynessList = new ArrayList<>(zoneIds.size());
        String[] busynessLevel = {"low", "medium", "high"};
        for (int i = 0; i < zoneIds.size(); i++) {
            busynessList.add(busynessLevel[i%3]);
        }

        HashMap<Long, String> zoneBusynessMap = new HashMap<>();
        for (int i = 0; i < zoneIds.size(); i++) {
            zoneBusynessMap.put(zoneIds.get(i), busynessList.get(i));
        }
        return zoneBusynessMap;
    }

    @Override
    public List<String> predictZoneBusyness(List<ZonedDateTime> zonedDateTimeList, Long zoneId) {
        // to integrate with the ML model, one fixed zone for multiple times
        ArrayList<String> busynessList = new ArrayList<>(zonedDateTimeList.size());
        String[] busynessLevel = {"low", "medium", "high"};
        for (int i = 0; i < zonedDateTimeList.size(); i++) {
            busynessList.add(busynessLevel[i%3]);
        }
        return busynessList;
    }
}
