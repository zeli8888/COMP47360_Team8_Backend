package team8.comp47360_team8_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.comp47360_team8_backend.dto.ZoneBusynessDTO;
import team8.comp47360_team8_backend.model.Zone;
import team8.comp47360_team8_backend.repository.ZoneRepository;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<ZoneBusynessDTO> predictZoneBusyness(LocalDateTime dateTime) {
        List<Zone> zones = zoneRepository.findAllByOrderByZoneIdAsc();
        List<ZoneBusynessDTO> zoneBusynessDTOs = new ArrayList<>(zones.size());
        for (Zone zone : zones) {
            zoneBusynessDTOs.add(new ZoneBusynessDTO(zone, 0));
        }
        return zoneBusynessDTOs;
    }
}
