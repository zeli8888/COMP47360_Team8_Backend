package team8.comp47360_team8_backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import team8.comp47360_team8_backend.model.User;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 18:01
 * @Version : V1.0
 * @Description :
 */
public interface UserService {
    User createUser(User user);
    User updateUser(User user);
    User getUser();
    void deleteUser();
    String updateUserPicture(MultipartFile file);
    Resource getUserPicture(String filename);
}
