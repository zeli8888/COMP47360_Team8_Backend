package team8.comp47360_team8_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team8.comp47360_team8_backend.model.User;

import java.util.Optional;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 17:35
 * @Version : V1.0
 * @Description :
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
}
