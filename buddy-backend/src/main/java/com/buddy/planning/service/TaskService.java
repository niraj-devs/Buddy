package com.buddy.planning.service;

import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.planning.dto.CreateTaskRequest;
import com.buddy.planning.dto.TaskResponse;
import com.buddy.planning.dto.UpdateTaskRequest;
import com.buddy.planning.entity.Milestone;
import com.buddy.planning.entity.Task;
import com.buddy.planning.repository.MilestoneRepository;
import com.buddy.planning.repository.TaskRepository;
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
public class TaskService {

    private static final String TODO = "TODO";
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String COMPLETED = "COMPLETED";
    private static final String CANCELLED = "CANCELLED";

    private static final Set<String> ALLOWED_PRIORITIES = Set.of(
            "LOW", "MEDIUM", "HIGH"
    );

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            TODO, IN_PROGRESS, COMPLETED, CANCELLED
    );

    private final TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public TaskService(
            TaskRepository taskRepository,
            MilestoneRepository milestoneRepository,
            UserRepository userRepository,
            StudentRepository studentRepository
    ) {
        this.taskRepository = taskRepository;
        this.milestoneRepository = milestoneRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public TaskResponse createTask(
            String email,
            Long milestoneId,
            CreateTaskRequest request
    ) {
        Student student = findStudentByEmail(email);
        Milestone milestone = findOwnedMilestone(
                milestoneId,
                student.getId()
        );

        Task task = new Task();
        task.setStudent(student);
        task.setMilestone(milestone);
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setPriority(normalizeAndValidatePriority(request.priority()));
        task.setDueDate(request.dueDate());
        task.setStatus(TODO);

        return mapToResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByMilestone(
            String email,
            Long milestoneId
    ) {
        Student student = findStudentByEmail(email);

        findOwnedMilestone(milestoneId, student.getId());

        return taskRepository.findByMilestoneId(milestoneId)
                .stream()
                .filter(task -> task.getStudent().getId()
                        .equals(student.getId()))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks(String email) {
        Student student = findStudentByEmail(email);

        return taskRepository.findByStudentId(student.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(String email, Long taskId) {
        Student student = findStudentByEmail(email);

        return mapToResponse(
                findOwnedTask(taskId, student.getId())
        );
    }

    @Transactional
    public TaskResponse updateTask(
            String email,
            Long taskId,
            UpdateTaskRequest request
    ) {
        Student student = findStudentByEmail(email);
        Task task = findOwnedTask(taskId, student.getId());

        if (request.title() != null) {
            String title = request.title().trim();

            if (title.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Title cannot be blank"
                );
            }

            if (title.length() > 200) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Title cannot exceed 200 characters"
                );
            }

            task.setTitle(title);
        }

        if (request.description() != null) {
            task.setDescription(request.description());
        }

        if (request.priority() != null) {
            task.setPriority(
                    normalizeAndValidatePriority(request.priority())
            );
        }

        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        if (request.status() != null) {
            String status = request.status().trim().toUpperCase();

            if (!ALLOWED_STATUSES.contains(status)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid task status. Allowed values: "
                                + ALLOWED_STATUSES
                );
            }

            task.setStatus(status);

            if (COMPLETED.equals(status)) {
                if (task.getCompletedAt() == null) {
                    task.setCompletedAt(LocalDateTime.now());
                }
            } else {
                task.setCompletedAt(null);
            }
        }

        return mapToResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(String email, Long taskId) {
        Student student = findStudentByEmail(email);
        Task task = findOwnedTask(taskId, student.getId());

        taskRepository.delete(task);
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

    private Task findOwnedTask(Long taskId, Long studentId) {
        return taskRepository.findByIdAndStudentId(taskId, studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));
    }

    private String normalizeAndValidatePriority(String priority) {
        String normalized = priority == null || priority.isBlank()
                ? "MEDIUM"
                : priority.trim().toUpperCase();

        if (!ALLOWED_PRIORITIES.contains(normalized)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid task priority. Allowed values: "
                            + ALLOWED_PRIORITIES
            );
        }

        return normalized;
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getMilestone().getId(),
                task.getStudent().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getDueDate(),
                task.getStatus(),
                task.getCompletedAt(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}