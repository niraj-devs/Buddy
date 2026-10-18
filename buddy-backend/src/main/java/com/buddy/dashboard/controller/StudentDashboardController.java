package com.buddy.dashboard.controller;

import com.buddy.dashboard.dto.StudentDashboardResponse;
import com.buddy.dashboard.service.StudentDashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class StudentDashboardController {

    private final StudentDashboardService dashboardService;

    public StudentDashboardController(
            StudentDashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/me")
    public StudentDashboardResponse getMyDashboard(
            Authentication authentication
    ) {
        return dashboardService.getMyDashboard(
                authentication.getName()
        );
    }
}