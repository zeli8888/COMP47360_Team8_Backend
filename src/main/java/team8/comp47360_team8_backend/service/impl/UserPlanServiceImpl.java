package team8.comp47360_team8_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team8.comp47360_team8_backend.exception.UnauthorizedAccessException;
import team8.comp47360_team8_backend.exception.UserPlanNotFoundException;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.model.UserPlan;
import team8.comp47360_team8_backend.repository.UserPlanRepository;
import team8.comp47360_team8_backend.repository.UserRepository;
import team8.comp47360_team8_backend.security.CustomUserDetails;
import team8.comp47360_team8_backend.service.UserPlanService;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 20:23
 * @Version : V1.0
 * @Description :
 */

@Service
public class UserPlanServiceImpl implements UserPlanService {
    @Autowired
    private UserPlanRepository userPlanRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserPlan> getUserPlans() {
        return userPlanRepository.findByUserId(getAuthenticatedUser().getUserId());
    }

    @Override
    public void deleteUserPlan(long userPlanId) {
        UserPlan userPlan = userPlanRepository.findById(userPlanId).orElseThrow(() -> new UserPlanNotFoundException(userPlanId));
        if (userPlan.getUser().getId() != getAuthenticatedUser().getUserId()) {
            throw new UnauthorizedAccessException("Access denied for deleting user plan with id: " + userPlanId);
        }
        userPlanRepository.deleteById(userPlanId);
    }

    @Override
    public UserPlan createUserPlan(UserPlan userPlan) {
        long userId = getAuthenticatedUser().getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Authenticated User Not Found!"));
        userPlan.setUser(user);
        userPlan.setUserPlanId(null);
        return userPlanRepository.save(userPlan);
    }

    @Override
    public UserPlan updateUserPlan(long userPlanId, UserPlan userPlan) {
        UserPlan managedUserPlan = userPlanRepository.findById(userPlanId).orElseThrow(() -> new UserPlanNotFoundException(userPlanId));
        User user = managedUserPlan.getUser();
        if (user.getId() != getAuthenticatedUser().getUserId()) {
            throw new UnauthorizedAccessException("Access denied for updating user plan with id: " + userPlan.getUserPlanId());
        }
        userPlan.setUserPlanId(userPlanId);
        userPlan.setUser(user);
        return userPlanRepository.save(userPlan);
    }

    @Override
    @Transactional
    public void deleteUserPlan(List<Long> userPlanIds) {
        for (long userPlanId : userPlanIds) {
            deleteUserPlan(userPlanId);
        }
    }

    @Override
    @Transactional
    public List<UserPlan> createUserPlan(List<UserPlan> userPlans) {
        long userId = getAuthenticatedUser().getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Authenticated User Not Found!"));
        for (UserPlan userPlan : userPlans) {
            userPlan.setUser(user);
            userPlan.setUserPlanId(null);
        }
        return userPlanRepository.saveAll(userPlans);
    }

    private CustomUserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
