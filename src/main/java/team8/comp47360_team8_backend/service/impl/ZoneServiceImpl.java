package team8.comp47360_team8_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.comp47360_team8_backend.model.Zone;
import team8.comp47360_team8_backend.repository.ZoneRepository;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.ZonedDateTime;
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
    public HashMap<Long, Double> predictZoneBusyness(ZonedDateTime dateTime) {
        List<Zone> zones = zoneRepository.findAllByOrderByZoneIdAsc();
        // to integrate with the ML model
        double[] busynesses = new double[zones.size()];
        for (int i = 0; i < busynesses.length; i++) {
            busynesses[i] = (i + 1);
        }
        HashMap<Long, Double> zoneBusynessMap = new HashMap<>();
        for (int i = 0; i < zones.size(); i++) {
            zoneBusynessMap.put(zones.get(i).getZoneId(), busynesses[i]);
        }
        return zoneBusynessMap;
    }
}
