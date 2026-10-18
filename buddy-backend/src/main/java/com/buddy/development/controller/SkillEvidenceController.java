package com.buddy.development.controller;

import com.buddy.development.dto.CreateSkillEvidenceRequest;
import com.buddy.development.dto.SkillEvidenceResponse;
import com.buddy.development.service.SkillEvidenceService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/development")
public class SkillEvidenceController {

    private final SkillEvidenceService evidenceService;

    public SkillEvidenceController(SkillEvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @PostMapping("/skills/{skillId}/evidence")
    public SkillEvidenceResponse submit(
            @PathVariable Long skillId,
            @Valid @RequestBody CreateSkillEvidenceRequest request,
            Authentication authentication
    ) {
        return evidenceService.submit(
                authentication.getName(), skillId, request
        );
    }

    @GetMapping("/skills/{skillId}/evidence")
    public List<SkillEvidenceResponse> getSkillEvidence(
            @PathVariable Long skillId,
            Authentication authentication
    ) {
        return evidenceService.getSkillEvidence(
                authentication.getName(), skillId
        );
    }

    @GetMapping("/evidence/me")
    public List<SkillEvidenceResponse> getMyEvidence(
            Authentication authentication
    ) {
        return evidenceService.getMyEvidence(authentication.getName());
    }
}