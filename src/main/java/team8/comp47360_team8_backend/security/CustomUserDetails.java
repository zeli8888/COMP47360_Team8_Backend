package team8.comp47360_team8_backend.security;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 20:43
 * @Version : V1.0
 * @Description :
 */

public class CustomUserDetails extends User {

    private long userId;

    public CustomUserDetails(String userName, String password, long userId, Set<GrantedAuthority> authorities) {
        super(userName, password, authorities);
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}