package team8.comp47360_team8_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 21:25
 * @Version : V1.0
 * @Description :
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserPlanNotFoundException extends RuntimeException {
    public UserPlanNotFoundException(long userPlanId) {
        super("User plan not found : " + userPlanId);
    }
}
