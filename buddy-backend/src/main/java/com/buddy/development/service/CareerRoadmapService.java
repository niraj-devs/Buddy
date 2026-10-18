package com.buddy.development.service;

import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.development.dto.CareerPathResponse;
import com.buddy.development.dto.RoadmapResponse;
import com.buddy.development.dto.RoadmapSkillResponse;
import com.buddy.development.entity.Roadmap;
import com.buddy.development.entity.RoadmapSkill;
import com.buddy.development.entity.StudentSkill;
import com.buddy.development.repository.RoadmapRepository;
import com.buddy.development.repository.RoadmapSkillRepository;
import com.buddy.development.repository.StudentSkillRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import com.buddy.development.entity.CareerPath;
import com.buddy.development.repository.CareerPathRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CareerRoadmapService {

    private final CareerPathRepository careerPathRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapSkillRepository roadmapSkillRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final SkillUnlockService skillUnlockService;

    public CareerRoadmapService(
            CareerPathRepository careerPathRepository,
            RoadmapRepository roadmapRepository,
            RoadmapSkillRepository roadmapSkillRepository,
            UserRepository userRepository,
            StudentRepository studentRepository,
            StudentSkillRepository studentSkillRepository,
            SkillUnlockService skillUnlockService
    ) {
        this.careerPathRepository = careerPathRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapSkillRepository = roadmapSkillRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.skillUnlockService = skillUnlockService;
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
        Roadmap roadmap = findActiveRoadmap(roadmapId);

        return roadmapSkillRepository
                .findByRoadmapIdOrderBySequenceOrder(roadmap.getId())
                .stream()
                .map(roadmapSkill -> toResponse(
                        roadmapSkill, true, List.of(),
                        "NOT_STARTED", BigDecimal.ZERO))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoadmapSkillResponse> getPersonalizedRoadmapSkills(
            Long roadmapId, String email
    ) {
        Roadmap roadmap = findActiveRoadmap(roadmapId);
        Student student = findStudentByEmail(email);

        Map<Long, StudentSkill> progressBySkillId = new HashMap<>();

        for (StudentSkill progress :
                studentSkillRepository.findByStudentId(student.getId())) {
            progressBySkillId.put(progress.getSkill().getId(), progress);
        }

        return roadmapSkillRepository
                .findByRoadmapIdOrderBySequenceOrder(roadmap.getId())
                .stream()
                .map(roadmapSkill -> {
                    Long skillId = roadmapSkill.getSkill().getId();

                    var unlockStatus =
                            skillUnlockService.getSkillUnlockStatus(email, skillId);

                    StudentSkill progress = progressBySkillId.get(skillId);

                    String status = progress == null
                            ? "NOT_STARTED"
                            : progress.getStatus();

                    BigDecimal percentage = progress == null
                            || progress.getProgressPercentage() == null
                            ? BigDecimal.ZERO
                            : progress.getProgressPercentage();

                    return toResponse(
                            roadmapSkill,
                            unlockStatus.unlocked(),
                            unlockStatus.unmetPrerequisites(),
                            status,
                            percentage
                    );
                })
                .toList();
    }

    private Student findStudentByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Authenticated user was not found"));

        return studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile has not been created"));
    }

    private Roadmap findActiveRoadmap(Long roadmapId) {
        return roadmapRepository.findById(roadmapId)
                .filter(Roadmap::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Roadmap not found"));
    }

    private RoadmapSkillResponse toResponse(
            RoadmapSkill roadmapSkill,
            boolean unlocked,
            List<String> unmetPrerequisites,
            String status,
            BigDecimal progressPercentage
    ) {
        return new RoadmapSkillResponse(
                roadmapSkill.getSkill().getId(),
                roadmapSkill.getSkill().getName(),
                roadmapSkill.getSkill().getCategory(),
                roadmapSkill.getSkill().getDescription(),
                roadmapSkill.getSequenceOrder(),
                roadmapSkill.isCore(),
                unlocked,
                unmetPrerequisites,
                status,
                progressPercentage
        );
    }
}
