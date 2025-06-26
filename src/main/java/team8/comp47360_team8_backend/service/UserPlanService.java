package team8.comp47360_team8_backend.service;

import team8.comp47360_team8_backend.model.UserPlan;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 19:04
 * @Version : V1.0
 * @Description :
 */
public interface UserPlanService {
    List<UserPlan> getUserPlans();
    void deleteUserPlan(long userPlanId);
    UserPlan createUserPlan(UserPlan userPlan);
    UserPlan updateUserPlan(long userPlanId, UserPlan userPlan);
}
