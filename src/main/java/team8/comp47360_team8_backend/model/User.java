package team8.comp47360_team8_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 17:05
 * @Version : V1.0
 * @Description :
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(name="user_id")
    @JsonIgnore
    private Long id;

    @Column(name="google_id", unique = true, nullable = true)
    @JsonIgnore
    private String googleId;

    @Column(name="user_password", nullable = true)
    private String password;

    @Column(name="user_email", unique = true, nullable = true)
    private String email;

    @Column(name="user_name", unique = true, nullable = true)
    private String userName;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
            CascadeType.REMOVE,
            CascadeType.REFRESH
    }, orphanRemoval = false)
    @JsonIgnore
    private Set<UserPlan> userPlans = new HashSet<>();

    @Column(name="user_picture", nullable = true)
    private String userPicture;

    public User() {
    }

    public User(Long id, String password, String email, String userName, String userPicture) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.userName = userName;
        this.userPicture = userPicture;
    }

    public User(Long id, String googleId, String password, String email, String userName, Set<UserPlan> userPlans, String userPicture) {
        this.id = id;
        this.googleId = googleId;
        this.password = password;
        this.email = email;
        this.userName = userName;
        this.userPlans = userPlans;
        this.userPicture = userPicture;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<UserPlan> getUserPlans() {
        return userPlans;
    }

    public void setUserPlans(Set<UserPlan> userPlans) {
        this.userPlans = userPlans;
    }
}
