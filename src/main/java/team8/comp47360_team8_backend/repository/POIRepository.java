package team8.comp47360_team8_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team8.comp47360_team8_backend.model.POI;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 23:09
 * @Version : V1.0
 * @Description :
 */

@Repository
public interface POIRepository extends JpaRepository<POI, Long> {
}
