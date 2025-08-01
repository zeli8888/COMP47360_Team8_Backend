package team8.comp47360_team8_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team8.comp47360_team8_backend.model.UserPlan;
import team8.comp47360_team8_backend.service.UserPlanService;

import java.util.List;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 20:20
 * @Version : V1.0
 * @Description :
 */
@RestController
public class UserPlanController {
    @Autowired
    private UserPlanService userPlanService;

    @GetMapping("/userplans")
    public ResponseEntity<List<UserPlan>> getUserPlans() {
        return ResponseEntity.ok(userPlanService.getUserPlans());
    }

    @PostMapping("/userplans")
    public ResponseEntity<UserPlan> createUserPlan(@RequestBody UserPlan userPlan) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userPlanService.createUserPlan(userPlan));
    }

    @PostMapping("/userplans/multiple")
    public ResponseEntity<List<UserPlan>> createUserPlan(@RequestBody List<UserPlan> userPlans) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userPlanService.createUserPlan(userPlans));
    }

    @DeleteMapping("/userplans/{userPlanId}")
    public ResponseEntity<Void> deleteUserPlan(@PathVariable long userPlanId) {
        userPlanService.deleteUserPlan(userPlanId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/userplans")
    public ResponseEntity<Void> deleteUserPlan(@RequestBody List<Long> userPlanIds) {
        userPlanService.deleteUserPlan(userPlanIds);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/userplans/{userPlanId}")
    public ResponseEntity<UserPlan> updateUserPlan(@PathVariable long userPlanId, @RequestBody UserPlan userPlan) {
        return ResponseEntity.ok(userPlanService.updateUserPlan(userPlanId, userPlan));
    }
}
