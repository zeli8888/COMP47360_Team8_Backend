package team8.comp47360_team8_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team8.comp47360_team8_backend.model.UserPlan;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 17:36
 * @Version : V1.0
 * @Description :
 */
public interface UserPlanRepository extends JpaRepository<UserPlan, Long> {
    List<UserPlan> findByUserId(Long userId);
    List<UserPlan> findByUserUserName(String userName);
}
