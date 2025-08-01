package team8.comp47360_team8_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team8.comp47360_team8_backend.model.Zone;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 23:10
 * @Version : V1.0
 * @Description :
 */

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    @Query("SELECT DISTINCT z.zoneId FROM Zone z")
    List<Long> findDistinctZoneIds();
}
