package com.buddy.planning.service;

import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.planning.dto.CreateGoalRequest;
import com.buddy.planning.dto.GoalResponse;
import com.buddy.planning.dto.UpdateGoalRequest;
import com.buddy.planning.entity.Goal;
import com.buddy.planning.repository.GoalRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class GoalService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "ACADEMIC", "CAREER", "SKILL",
            "PROJECT", "PERSONAL", "OTHER"
    );

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "NOT_STARTED", "IN_PROGRESS",
            "COMPLETED", "CANCELLED"
    );

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public GoalService(
            GoalRepository goalRepository,
            UserRepository userRepository,
            StudentRepository studentRepository
    ) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public GoalResponse createGoal(String email, CreateGoalRequest request) {
        Student student = findStudentByEmail(email);
        String goalType = normalizeAndValidateType(request.goalType());

        Goal goal = new Goal();
        goal.setStudent(student);
        goal.setTitle(request.title().trim());
        goal.setDescription(request.description());
        goal.setGoalType(goalType);
        goal.setTargetDate(request.targetDate());
        goal.setStatus("NOT_STARTED");

        return mapToResponse(goalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getMyGoals(String email) {
        Student student = findStudentByEmail(email);

        return goalRepository.findByStudentId(student.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse getGoal(String email, Long goalId) {
        Student student = findStudentByEmail(email);
        return mapToResponse(findOwnedGoal(goalId, student.getId()));
    }

    @Transactional
    public GoalResponse updateGoal(
            String email,
            Long goalId,
            UpdateGoalRequest request
    ) {
        Student student = findStudentByEmail(email);
        Goal goal = findOwnedGoal(goalId, student.getId());

        if (request.title() != null) {
            String title = request.title().trim();

            if (title.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Title cannot be blank"
                );
            }

            goal.setTitle(title);
        }

        if (request.description() != null) {
            goal.setDescription(request.description());
        }

        if (request.goalType() != null) {
            goal.setGoalType(normalizeAndValidateType(request.goalType()));
        }

        if (request.targetDate() != null) {
            goal.setTargetDate(request.targetDate());
        }

        if (request.status() != null) {
            String status = request.status().trim().toUpperCase();

            if (!ALLOWED_STATUSES.contains(status)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid goal status. Allowed values: " + ALLOWED_STATUSES
                );
            }

            goal.setStatus(status);
        }

        return mapToResponse(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(String email, Long goalId) {
        Student student = findStudentByEmail(email);
        Goal goal = findOwnedGoal(goalId, student.getId());
        goalRepository.delete(goal);
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

    private String normalizeAndValidateType(String goalType) {
        String normalized = goalType == null
                ? ""
                : goalType.trim().toUpperCase();

        if (!ALLOWED_TYPES.contains(normalized)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid goal type. Allowed values: " + ALLOWED_TYPES
            );
        }

        return normalized;
    }

    private GoalResponse mapToResponse(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getGoalType(),
                goal.getTargetDate(),
                goal.getStatus(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
}