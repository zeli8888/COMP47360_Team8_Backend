package team8.comp47360_team8_backend.service;

import team8.comp47360_team8_backend.model.User;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 18:01
 * @Version : V1.0
 * @Description :
 */
public interface UserService {
    public User createUser(User user);
    public User updateUser(User user, String userEmail);
}
