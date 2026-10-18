package com.buddy.planning.service;

import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.planning.dto.CreateMilestoneRequest;
import com.buddy.planning.dto.MilestoneResponse;
import com.buddy.planning.dto.UpdateMilestoneRequest;
import com.buddy.planning.entity.Goal;
import com.buddy.planning.entity.Milestone;
import com.buddy.planning.repository.GoalRepository;
import com.buddy.planning.repository.MilestoneRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class MilestoneService {

    private static final String NOT_STARTED = "NOT_STARTED";
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String COMPLETED = "COMPLETED";

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            NOT_STARTED,
            IN_PROGRESS,
            COMPLETED
    );

    private final MilestoneRepository milestoneRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public MilestoneService(
            MilestoneRepository milestoneRepository,
            GoalRepository goalRepository,
            UserRepository userRepository,
            StudentRepository studentRepository
    ) {
        this.milestoneRepository = milestoneRepository;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public MilestoneResponse createMilestone(
            String email,
            Long goalId,
            CreateMilestoneRequest request
    ) {
        Student student = findStudentByEmail(email);
        Goal goal = findOwnedGoal(goalId, student.getId());

        if (milestoneRepository.existsByGoalIdAndSequenceNumber(
                goalId,
                request.sequenceNumber()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This goal already has a milestone with that sequence number"
            );
        }

        Milestone milestone = new Milestone();
        milestone.setGoal(goal);
        milestone.setTitle(request.title().trim());
        milestone.setDescription(request.description());
        milestone.setSequenceNumber(request.sequenceNumber());
        milestone.setTargetDate(request.targetDate());
        milestone.setStatus(NOT_STARTED);

        return mapToResponse(milestoneRepository.save(milestone));
    }

    @Transactional(readOnly = true)
    public List<MilestoneResponse> getMilestones(
            String email,
            Long goalId
    ) {
        Student student = findStudentByEmail(email);
        findOwnedGoal(goalId, student.getId());

        return milestoneRepository
                .findByGoalIdOrderBySequenceNumber(goalId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MilestoneResponse getMilestone(
            String email,
            Long milestoneId
    ) {
        Student student = findStudentByEmail(email);

        return mapToResponse(
                findOwnedMilestone(milestoneId, student.getId())
        );
    }

    @Transactional
    public MilestoneResponse updateMilestone(
            String email,
            Long milestoneId,
            UpdateMilestoneRequest request
    ) {
        Student student = findStudentByEmail(email);
        Milestone milestone = findOwnedMilestone(
                milestoneId,
                student.getId()
        );

        if (request.title() != null) {
            String title = request.title().trim();

            if (title.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Title cannot be blank"
                );
            }

            milestone.setTitle(title);
        }

        if (request.description() != null) {
            milestone.setDescription(request.description());
        }

        if (request.sequenceNumber() != null
                && !request.sequenceNumber().equals(
                        milestone.getSequenceNumber()
                )) {

            if (milestoneRepository.existsByGoalIdAndSequenceNumber(
                    milestone.getGoal().getId(),
                    request.sequenceNumber()
            )) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This goal already has a milestone with that sequence number"
                );
            }

            milestone.setSequenceNumber(request.sequenceNumber());
        }

        if (request.targetDate() != null) {
            milestone.setTargetDate(request.targetDate());
        }

        if (request.status() != null) {
            String status = request.status().trim().toUpperCase();

            if (!ALLOWED_STATUSES.contains(status)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid milestone status. Allowed values: "
                                + ALLOWED_STATUSES
                );
            }

            milestone.setStatus(status);

            if (COMPLETED.equals(status)) {
                if (milestone.getCompletedAt() == null) {
                    milestone.setCompletedAt(LocalDateTime.now());
                }
            } else {
                milestone.setCompletedAt(null);
            }
        }

        return mapToResponse(milestoneRepository.save(milestone));
    }

    @Transactional
    public void deleteMilestone(String email, Long milestoneId) {
        Student student = findStudentByEmail(email);

        Milestone milestone = findOwnedMilestone(
                milestoneId,
                student.getId()
        );

        milestoneRepository.delete(milestone);
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

    private Goal findOwnedGoal(Long goalId, Long studentId) {
        return goalRepository.findByIdAndStudentId(goalId, studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Goal not found"
                ));
    }

    private Milestone findOwnedMilestone(
            Long milestoneId,
            Long studentId
    ) {
        return milestoneRepository
                .findByIdAndGoalStudentId(milestoneId, studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Milestone not found"
                ));
    }

    private MilestoneResponse mapToResponse(Milestone milestone) {
        return new MilestoneResponse(
                milestone.getId(),
                milestone.getGoal().getId(),
                milestone.getTitle(),
                milestone.getDescription(),
                milestone.getSequenceNumber(),
                milestone.getTargetDate(),
                milestone.getStatus(),
                milestone.getCompletedAt(),
                milestone.getCreatedAt(),
                milestone.getUpdatedAt()
        );
    }
}