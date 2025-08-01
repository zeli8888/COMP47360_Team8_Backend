package team8.comp47360_team8_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team8.comp47360_team8_backend.model.POIType;

import java.util.Optional;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 21:28
 * @Version : V1.0
 * @Description :
 */

@Repository
public interface POITypeRepository extends JpaRepository<POIType, Long> {
    Optional<POIType> getByPoiTypeName(String poiTypeName);
}
