package com.buddy.development.service;

import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.development.dto.RoadmapProgressResponse;
import com.buddy.development.dto.StudentSkillResponse;
import com.buddy.development.entity.Roadmap;
import com.buddy.development.entity.RoadmapSkill;
import com.buddy.development.entity.Skill;
import com.buddy.development.entity.StudentSkill;
import com.buddy.development.repository.RoadmapRepository;
import com.buddy.development.repository.RoadmapSkillRepository;
import com.buddy.development.repository.SkillRepository;
import com.buddy.development.repository.StudentSkillRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentSkillService {

    private static final String NOT_STARTED = "NOT_STARTED";
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String COMPLETED = "COMPLETED";

    private final StudentSkillRepository studentSkillRepository;
    private final SkillRepository skillRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapSkillRepository roadmapSkillRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final SkillUnlockService skillUnlockService;

    public StudentSkillService(
            StudentSkillRepository studentSkillRepository,
            SkillRepository skillRepository,
            RoadmapRepository roadmapRepository,
            RoadmapSkillRepository roadmapSkillRepository,
            UserRepository userRepository,
            StudentRepository studentRepository,
            SkillUnlockService skillUnlockService
    ) {
        this.studentSkillRepository = studentSkillRepository;
        this.skillRepository = skillRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapSkillRepository = roadmapSkillRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.skillUnlockService = skillUnlockService;
    }
    @Transactional(readOnly = true)
    public List<StudentSkillResponse> getMySkills(String email) {
        Student student = findStudentByEmail(email);

        Roadmap roadmap = roadmapRepository.findAll()
                .stream()
                .filter(Roadmap::isActive)
                .filter(candidate -> candidate.getCareerPath().getId()
                        .equals(student.getCareerPath().getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No active roadmap found for your career path"
                ));

        Map<Long, StudentSkill> progressBySkillId = new HashMap<>();

        for (StudentSkill studentSkill :
                studentSkillRepository.findByStudentId(student.getId())) {
            progressBySkillId.put(
                    studentSkill.getSkill().getId(),
                    studentSkill
            );
        }

        return roadmapSkillRepository
                .findByRoadmapIdOrderBySequenceOrder(roadmap.getId())
                .stream()
                .map(roadmapSkill -> {
                    Skill skill = roadmapSkill.getSkill();

                    StudentSkill progress =
                            progressBySkillId.get(skill.getId());

                    if (progress != null) {
                        return mapToResponse(progress);
                    }

                    return new StudentSkillResponse(
                            skill.getId(),
                            skill.getName(),
                            skill.getCategory(),
                            NOT_STARTED,
                            BigDecimal.ZERO.setScale(2),
                            null,
                            null,
                            null
                    );
                })
                .toList();
    }

    @Transactional
    public StudentSkillResponse updateProgress(
            String email,
            Long skillId,
            BigDecimal percentage
    ) {
        if (percentage == null
                || percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Progress percentage must be between 0 and 100"
            );
        }

        Student student = findStudentByEmail(email);

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Skill not found"
                ));

        if (!skillUnlockService.isSkillUnlocked(student.getId(), skillId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Skill is locked. Complete its prerequisites first."
            );
        }

        StudentSkill studentSkill = studentSkillRepository
                .findByStudentIdAndSkillId(student.getId(), skillId)
                .orElseGet(() -> {
                    StudentSkill newSkill = new StudentSkill();
                    newSkill.setStudent(student);
                    newSkill.setSkill(skill);
                    newSkill.setStatus(NOT_STARTED);
                    newSkill.setProgressPercentage(BigDecimal.ZERO);
                    newSkill.setSource("SELF_REPORTED");
                    return newSkill;
                });

        if (COMPLETED.equals(studentSkill.getStatus())
                && percentage.compareTo(new BigDecimal("100")) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Completed skill progress cannot be reduced"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (studentSkill.getStartedAt() == null) {
            studentSkill.setStartedAt(now);
        }

        if (percentage.compareTo(new BigDecimal("100")) == 0) {
            studentSkill.setStatus(COMPLETED);
            studentSkill.setProgressPercentage(new BigDecimal("100.00"));

            if (studentSkill.getAchievedAt() == null) {
                studentSkill.setAchievedAt(now);
            }
        } else if (percentage.compareTo(BigDecimal.ZERO) == 0) {
            studentSkill.setStatus(NOT_STARTED);
            studentSkill.setProgressPercentage(BigDecimal.ZERO);
            studentSkill.setAchievedAt(null);
        } else {
            studentSkill.setStatus(IN_PROGRESS);
            studentSkill.setProgressPercentage(percentage);
            studentSkill.setAchievedAt(null);
        }

        return mapToResponse(studentSkillRepository.save(studentSkill));
    }

    @Transactional(readOnly = true)
    public RoadmapProgressResponse getRoadmapProgress(
            String email,
            Long roadmapId
    ) {
        Student student = findStudentByEmail(email);

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Roadmap not found"
                ));

        List<RoadmapSkill> roadmapSkills =
                roadmapSkillRepository
                        .findByRoadmapIdOrderBySequenceOrder(roadmapId);

        Map<Long, StudentSkill> progressBySkillId = new HashMap<>();

        for (StudentSkill studentSkill :
                studentSkillRepository.findByStudentId(student.getId())) {
            progressBySkillId.put(
                    studentSkill.getSkill().getId(),
                    studentSkill
            );
        }

        List<StudentSkillResponse> skillResponses = roadmapSkills.stream()
                .map(roadmapSkill -> {
                    StudentSkill studentSkill = progressBySkillId.get(
                            roadmapSkill.getSkill().getId()
                    );

                    if (studentSkill != null) {
                        return mapToResponse(studentSkill);
                    }

                    Skill skill = roadmapSkill.getSkill();

                    return new StudentSkillResponse(
                            skill.getId(),
                            skill.getName(),
                            skill.getCategory(),
                            NOT_STARTED,
                            BigDecimal.ZERO.setScale(2),
                            null,
                            null,
                            null
                    );
                })
                .toList();

        int totalSkills = roadmapSkills.size();

        int completedSkills = (int) skillResponses.stream()
                .filter(skill -> COMPLETED.equals(skill.status()))
                .count();

        BigDecimal percentage = totalSkills == 0
                ? BigDecimal.ZERO.setScale(2)
                : BigDecimal.valueOf(completedSkills)
                        .multiply(new BigDecimal("100"))
                        .divide(
                                BigDecimal.valueOf(totalSkills),
                                2,
                                java.math.RoundingMode.HALF_UP
                        );

        return new RoadmapProgressResponse(
                roadmap.getId(),
                roadmap.getName(),
                totalSkills,
                completedSkills,
                percentage,
                skillResponses
        );
    }

    private Student findStudentByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Authenticated user was not found"
                ));

        return studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile has not been created"
                ));
    }

    private StudentSkillResponse mapToResponse(StudentSkill studentSkill) {
        Skill skill = studentSkill.getSkill();

        return new StudentSkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getCategory(),
                studentSkill.getStatus(),
                studentSkill.getProgressPercentage(),
                studentSkill.getProficiencyLevel(),
                studentSkill.getStartedAt(),
                studentSkill.getAchievedAt()
        );
    }
}
