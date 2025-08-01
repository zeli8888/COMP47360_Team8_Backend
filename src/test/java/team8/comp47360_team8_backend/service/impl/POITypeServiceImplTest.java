package team8.comp47360_team8_backend.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team8.comp47360_team8_backend.repository.POITypeRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @Author : Ze Li
 * @Date : 03/07/2025 18:32
 * @Version : V1.0
 * @Description :
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class POITypeServiceImplTest {
    @Mock
    private POITypeRepository poiTypeRepository;
    @InjectMocks
    private POITypeServiceImpl poiTypeService = new POITypeServiceImpl();

    @Test
    void getAllPOITypes() {
        when(poiTypeRepository.findAll()).thenReturn(new ArrayList<>());
        assertEquals(0, poiTypeService.getAllPOITypes().size());
    }
}