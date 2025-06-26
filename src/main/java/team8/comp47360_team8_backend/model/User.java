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
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue
    @Column(name="user_id")
    @JsonIgnore
    private Long id;

    @Column(name="user_passwd")
    private String password;

    @Column(name="user_email", unique = true)
    private String email;

    @Column(name="user_name", unique = true, nullable = false)
    private String userName;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
            CascadeType.REMOVE,
            CascadeType.REFRESH
    }, orphanRemoval = false)
    @JsonIgnore
    private Set<UserPlan> userPlans = new HashSet<>();

    public User() {
    }

    public User(Long id, String password, String email, String userName) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.userName = userName;
    }

    public User(Long id, String password, String email, String userName, Set<UserPlan> userPlans) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.userName = userName;
        this.userPlans = userPlans;
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
