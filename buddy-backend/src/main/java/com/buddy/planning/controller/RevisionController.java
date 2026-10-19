package com.buddy.planning.controller;

import com.buddy.planning.dto.CreateRevisionRequest;
import com.buddy.planning.dto.RevisionResponse;
import com.buddy.planning.service.RevisionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/revisions")
public class RevisionController {

    private final RevisionService revisionService;

    public RevisionController(RevisionService revisionService) {
        this.revisionService = revisionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RevisionResponse scheduleRevision(
            Authentication authentication,
            @Valid @RequestBody CreateRevisionRequest request
    ) {
        return revisionService.scheduleRevision(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/me")
    public List<RevisionResponse> getMyRevisions(
            Authentication authentication
    ) {
        return revisionService.getMyRevisions(authentication.getName());
    }

    @GetMapping("/me/today")
    public List<RevisionResponse> getTodaysRevisions(
            Authentication authentication
    ) {
        return revisionService.getTodaysRevisions(authentication.getName());
    }

    @GetMapping("/me/upcoming")
    public List<RevisionResponse> getUpcomingRevisions(
            Authentication authentication
    ) {
        return revisionService.getUpcomingRevisions(authentication.getName());
    }

    @PatchMapping("/{revisionId}/complete")
    public RevisionResponse completeRevision(
            Authentication authentication,
            @PathVariable Long revisionId
    ) {
        return revisionService.completeRevision(
                authentication.getName(),
                revisionId
        );
    }

    @PatchMapping("/{revisionId}/miss")
    public RevisionResponse markRevisionMissed(
            Authentication authentication,
            @PathVariable Long revisionId
    ) {
        return revisionService.markRevisionMissed(
                authentication.getName(),
                revisionId
        );
    }
}