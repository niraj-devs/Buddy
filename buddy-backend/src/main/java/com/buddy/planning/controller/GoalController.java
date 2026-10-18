package com.buddy.planning.controller;

import com.buddy.planning.dto.CreateGoalRequest;
import com.buddy.planning.dto.GoalResponse;
import com.buddy.planning.dto.UpdateGoalRequest;
import com.buddy.planning.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse createGoal(
            @Valid @RequestBody CreateGoalRequest request,
            Authentication authentication
    ) {
        return goalService.createGoal(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/me")
    public List<GoalResponse> getMyGoals(
            Authentication authentication
    ) {
        return goalService.getMyGoals(authentication.getName());
    }

    @GetMapping("/{goalId}")
    public GoalResponse getGoal(
            @PathVariable Long goalId,
            Authentication authentication
    ) {
        return goalService.getGoal(
                authentication.getName(),
                goalId
        );
    }

    @PatchMapping("/{goalId}")
    public GoalResponse updateGoal(
            @PathVariable Long goalId,
            @Valid @RequestBody UpdateGoalRequest request,
            Authentication authentication
    ) {
        return goalService.updateGoal(
                authentication.getName(),
                goalId,
                request
        );
    }

    @DeleteMapping("/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(
            @PathVariable Long goalId,
            Authentication authentication
    ) {
        goalService.deleteGoal(
                authentication.getName(),
                goalId
        );
    }
}