package com.buddy.development.controller;

import com.buddy.development.dto.CareerPathResponse;
import com.buddy.development.dto.RoadmapResponse;
import com.buddy.development.dto.RoadmapSkillResponse;
import com.buddy.development.service.CareerRoadmapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/development")
public class CareerRoadmapController {

    private final CareerRoadmapService careerRoadmapService;

    public CareerRoadmapController(
            CareerRoadmapService careerRoadmapService) {
        this.careerRoadmapService = careerRoadmapService;
    }

    @GetMapping("/career-paths")
    public List<CareerPathResponse> getCareerPaths() {
        return careerRoadmapService.getActiveCareerPaths();
    }

    @GetMapping("/career-paths/{careerPathId}/roadmaps")
    public List<RoadmapResponse> getRoadmaps(
            @PathVariable Long careerPathId) {
        return careerRoadmapService.getActiveRoadmaps(careerPathId);
    }

    @GetMapping("/roadmaps/{roadmapId}/skills")
    public List<RoadmapSkillResponse> getRoadmapSkills(
            @PathVariable Long roadmapId) {
        return careerRoadmapService.getRoadmapSkills(roadmapId);
    }
}