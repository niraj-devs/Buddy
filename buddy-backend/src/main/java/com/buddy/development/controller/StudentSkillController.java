package com.buddy.development.controller;

import com.buddy.development.dto.RoadmapProgressResponse;
import com.buddy.development.dto.StudentSkillResponse;
import com.buddy.development.dto.UpdateSkillProgressRequest;
import com.buddy.development.service.StudentSkillService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/development")
public class StudentSkillController {

    private final StudentSkillService studentSkillService;

    public StudentSkillController(
            StudentSkillService studentSkillService
    ) {
        this.studentSkillService = studentSkillService;
    }

    @GetMapping("/skills/me")
    public List<StudentSkillResponse> getMySkills(
            Authentication authentication
    ) {
        return studentSkillService.getMySkills(
                authentication.getName()
        );
    }

    @PutMapping("/skills/{skillId}/progress")
    public StudentSkillResponse updateProgress(
            @PathVariable Long skillId,
            @Valid @RequestBody UpdateSkillProgressRequest request,
            Authentication authentication
    ) {
        return studentSkillService.updateProgress(
                authentication.getName(),
                skillId,
                request.progressPercentage()
        );
    }

    @GetMapping("/roadmaps/{roadmapId}/progress")
    public RoadmapProgressResponse getRoadmapProgress(
            @PathVariable Long roadmapId,
            Authentication authentication
    ) {
        return studentSkillService.getRoadmapProgress(
                authentication.getName(),
                roadmapId
        );
    }
}
