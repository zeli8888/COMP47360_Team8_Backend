package team8.comp47360_team8_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.comp47360_team8_backend.model.POIType;
import team8.comp47360_team8_backend.repository.POITypeRepository;
import team8.comp47360_team8_backend.service.POITypeService;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 21:59
 * @Version : V1.0
 * @Description :
 */
@Service
public class POITypeServiceImpl implements POITypeService {
    @Autowired
    private POITypeRepository poiTypeRepository;

    @Override
    public List<POIType> getAllPOITypes() {
        return poiTypeRepository.findAll();
    }
}
