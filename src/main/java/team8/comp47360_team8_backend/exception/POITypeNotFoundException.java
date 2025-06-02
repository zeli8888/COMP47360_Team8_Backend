package team8.comp47360_team8_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 22:19
 * @Version : V1.0
 * @Description :
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class POITypeNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 179856888745840942L;

    public POITypeNotFoundException(String poiTypeName) {
        super("POI type not found : " + poiTypeName);
    }
}
