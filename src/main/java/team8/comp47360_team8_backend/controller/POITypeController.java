package team8.comp47360_team8_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import team8.comp47360_team8_backend.model.POIType;
import team8.comp47360_team8_backend.service.POITypeService;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 22:24
 * @Version : V1.0
 * @Description :
 */

@RestController
public class POITypeController {
    @Autowired
    private POITypeService poiTypeService;

    @GetMapping("/poitypes")
    public ResponseEntity<List<POIType>> getAllPOITypes() {
        return ResponseEntity.ok(poiTypeService.getAllPOITypes());
    }
}
