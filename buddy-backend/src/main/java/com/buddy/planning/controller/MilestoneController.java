package com.buddy.planning.controller;

import com.buddy.planning.dto.CreateMilestoneRequest;
import com.buddy.planning.dto.MilestoneResponse;
import com.buddy.planning.dto.UpdateMilestoneRequest;
import com.buddy.planning.service.MilestoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MilestoneController {

    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @PostMapping("/api/goals/{goalId}/milestones")
    @ResponseStatus(HttpStatus.CREATED)
    public MilestoneResponse createMilestone(
            @PathVariable Long goalId,
            @Valid @RequestBody CreateMilestoneRequest request,
            Authentication authentication
    ) {
        return milestoneService.createMilestone(
                authentication.getName(),
                goalId,
                request
        );
    }

    @GetMapping("/api/goals/{goalId}/milestones")
    public List<MilestoneResponse> getMilestones(
            @PathVariable Long goalId,
            Authentication authentication
    ) {
        return milestoneService.getMilestones(
                authentication.getName(),
                goalId
        );
    }

    @GetMapping("/api/milestones/{milestoneId}")
    public MilestoneResponse getMilestone(
            @PathVariable Long milestoneId,
            Authentication authentication
    ) {
        return milestoneService.getMilestone(
                authentication.getName(),
                milestoneId
        );
    }

    @PatchMapping("/api/milestones/{milestoneId}")
    public MilestoneResponse updateMilestone(
            @PathVariable Long milestoneId,
            @Valid @RequestBody UpdateMilestoneRequest request,
            Authentication authentication
    ) {
        return milestoneService.updateMilestone(
                authentication.getName(),
                milestoneId,
                request
        );
    }

    @DeleteMapping("/api/milestones/{milestoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMilestone(
            @PathVariable Long milestoneId,
            Authentication authentication
    ) {
        milestoneService.deleteMilestone(
                authentication.getName(),
                milestoneId
        );
    }
}