package com.buddy.planning.controller;

import com.buddy.planning.dto.CreateTaskRequest;
import com.buddy.planning.dto.TaskResponse;
import com.buddy.planning.dto.UpdateTaskRequest;
import com.buddy.planning.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/milestones/{milestoneId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            Authentication authentication,
            @PathVariable Long milestoneId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        return taskService.createTask(
                authentication.getName(),
                milestoneId,
                request
        );
    }

    @GetMapping("/milestones/{milestoneId}/tasks")
    public List<TaskResponse> getTasksByMilestone(
            Authentication authentication,
            @PathVariable Long milestoneId
    ) {
        return taskService.getTasksByMilestone(
                authentication.getName(),
                milestoneId
        );
    }

    @GetMapping("/tasks/me")
    public List<TaskResponse> getMyTasks(
            Authentication authentication
    ) {
        return taskService.getMyTasks(authentication.getName());
    }

    @GetMapping("/tasks/{taskId}")
    public TaskResponse getTask(
            Authentication authentication,
            @PathVariable Long taskId
    ) {
        return taskService.getTask(
                authentication.getName(),
                taskId
        );
    }

    @PatchMapping("/tasks/{taskId}")
    public TaskResponse updateTask(
            Authentication authentication,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        return taskService.updateTask(
                authentication.getName(),
                taskId,
                request
        );
    }

    @DeleteMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            Authentication authentication,
            @PathVariable Long taskId
    ) {
        taskService.deleteTask(
                authentication.getName(),
                taskId
        );
    }
}