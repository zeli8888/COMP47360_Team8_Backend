package team8.comp47360_team8_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.comp47360_team8_backend.exception.POITypeNotFoundException;
import team8.comp47360_team8_backend.model.POI;
import team8.comp47360_team8_backend.model.POIType;
import team8.comp47360_team8_backend.repository.POITypeRepository;
import team8.comp47360_team8_backend.service.POIService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 22:06
 * @Version : V1.0
 * @Description :
 */

@Service
public class POIServiceImpl implements POIService {
    @Autowired
    private POITypeRepository poiTypeRepository;

    @Override
    public Set<POI> getPOIsByPOITypeName(String poiTypeName) {
        POIType poiType = poiTypeRepository.getByPoiTypeName(poiTypeName).orElseThrow(() -> new POITypeNotFoundException(poiTypeName));
        return poiType.getPOIs();
    }
}