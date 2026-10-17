package com.buddy.academic.controller;

import com.buddy.academic.dto.progress.TopicProgressResponse;
import com.buddy.academic.dto.progress.TopicUnlockResponse;
import com.buddy.academic.dto.progress.UpdateTopicProgressRequest;
import com.buddy.academic.service.StudentProgressService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class StudentProgressController {

    private final StudentProgressService studentProgressService;

    public StudentProgressController(
            StudentProgressService studentProgressService
    ) {
        this.studentProgressService = studentProgressService;
    }

    @PostMapping("/topics/{topicId}/start")
    public TopicProgressResponse startTopic(
            @PathVariable Long topicId,
            Authentication authentication
    ) {
        return studentProgressService.startTopic(
                authentication.getName(),
                topicId
        );
    }

    @PatchMapping("/topics/{topicId}")
    public TopicProgressResponse updateProgress(
            @PathVariable Long topicId,
            @Valid @RequestBody UpdateTopicProgressRequest request,
            Authentication authentication
    ) {
        return studentProgressService.updateProgress(
                authentication.getName(),
                topicId,
                request.progressPercentage()
        );
    }

    @PostMapping("/topics/{topicId}/complete")
    public TopicProgressResponse completeTopic(
            @PathVariable Long topicId,
            Authentication authentication
    ) {
        return studentProgressService.completeTopic(
                authentication.getName(),
                topicId
        );
    }

    @GetMapping("/me")
    public List<TopicProgressResponse> getMyProgress(
            Authentication authentication
    ) {
        return studentProgressService.getMyProgress(
                authentication.getName()
        );
    }

    @GetMapping("/topics/unlocked")
    public List<TopicUnlockResponse> getUnlockedTopics(
            Authentication authentication
    ) {
        return studentProgressService.getUnlockedTopics(
                authentication.getName()
        );
    }
}