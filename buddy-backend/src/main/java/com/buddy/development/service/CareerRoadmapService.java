package com.buddy.development.service;

import com.buddy.development.dto.CareerPathResponse;
import com.buddy.development.dto.RoadmapResponse;
import com.buddy.development.dto.RoadmapSkillResponse;
import com.buddy.development.entity.CareerPath;
import com.buddy.development.entity.Roadmap;
import com.buddy.development.entity.RoadmapSkill;
import com.buddy.development.repository.CareerPathRepository;
import com.buddy.development.repository.RoadmapRepository;
import com.buddy.development.repository.RoadmapSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CareerRoadmapService {

    private final CareerPathRepository careerPathRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapSkillRepository roadmapSkillRepository;

    public CareerRoadmapService(
            CareerPathRepository careerPathRepository,
            RoadmapRepository roadmapRepository,
            RoadmapSkillRepository roadmapSkillRepository) {
        this.careerPathRepository = careerPathRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapSkillRepository = roadmapSkillRepository;
    }

    @Transactional(readOnly = true)
    public List<CareerPathResponse> getActiveCareerPaths() {
        return careerPathRepository.findAll()
                .stream()
                .filter(CareerPath::isActive)
                .map(path -> new CareerPathResponse(
                        path.getId(),
                        path.getName(),
                        path.getDescription()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoadmapResponse> getActiveRoadmaps(Long careerPathId) {
        CareerPath careerPath = careerPathRepository.findById(careerPathId)
                .filter(CareerPath::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Career path not found"));

        return roadmapRepository.findByCareerPathId(careerPath.getId())
                .stream()
                .filter(Roadmap::isActive)
                .map(roadmap -> new RoadmapResponse(
                        roadmap.getId(),
                        careerPath.getId(),
                        roadmap.getName(),
                        roadmap.getDescription(),
                        roadmap.getVersion()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoadmapSkillResponse> getRoadmapSkills(Long roadmapId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .filter(Roadmap::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Roadmap not found"));

        return roadmapSkillRepository
                .findByRoadmapIdOrderBySequenceOrder(roadmap.getId())
                .stream()
                .map(this::toRoadmapSkillResponse)
                .toList();
    }

    private RoadmapSkillResponse toRoadmapSkillResponse(
            RoadmapSkill roadmapSkill) {
        return new RoadmapSkillResponse(
                roadmapSkill.getSkill().getId(),
                roadmapSkill.getSkill().getName(),
                roadmapSkill.getSkill().getCategory(),
                roadmapSkill.getSkill().getDescription(),
                roadmapSkill.getSequenceOrder(),
                roadmapSkill.isCore());
    }
}