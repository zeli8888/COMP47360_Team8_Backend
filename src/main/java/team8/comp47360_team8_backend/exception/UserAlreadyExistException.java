package team8.comp47360_team8_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 19:36
 * @Version : V1.0
 * @Description :
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String username) {
        super("user already registered: " + username);
    }
}
