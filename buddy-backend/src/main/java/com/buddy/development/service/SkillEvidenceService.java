package com.buddy.development.service;

import com.buddy.development.dto.CreateSkillEvidenceRequest;
import com.buddy.development.dto.SkillEvidenceResponse;
import com.buddy.development.entity.Skill;
import com.buddy.development.entity.SkillEvidence;
import com.buddy.development.repository.SkillEvidenceRepository;
import com.buddy.development.repository.SkillRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SkillEvidenceService {

    private final SkillEvidenceRepository evidenceRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public SkillEvidenceService(
            SkillEvidenceRepository evidenceRepository,
            SkillRepository skillRepository,
            UserRepository userRepository,
            StudentRepository studentRepository
    ) {
        this.evidenceRepository = evidenceRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public SkillEvidenceResponse submit(
            String email,
            Long skillId,
            CreateSkillEvidenceRequest request
    ) {
        Student student = findStudent(email);

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Skill not found"
                ));

        SkillEvidence evidence = new SkillEvidence();
        evidence.setStudent(student);
        evidence.setSkill(skill);
        evidence.setEvidenceType(request.evidenceType());
        evidence.setTitle(request.title().trim());
        evidence.setDescription(request.description());
        evidence.setReferenceUrl(request.referenceUrl());
        evidence.setEvidenceDate(request.evidenceDate());
        evidence.setVerificationStatus("PENDING");

        return toResponse(evidenceRepository.save(evidence));
    }

    @Transactional
    public List<SkillEvidenceResponse> getMyEvidence(String email) {
        Student student = findStudent(email);

        return evidenceRepository.findByStudentId(student.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<SkillEvidenceResponse> getSkillEvidence(
            String email,
            Long skillId
    ) {
        Student student = findStudent(email);

        if (!skillRepository.existsById(skillId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Skill not found"
            );
        }

        return evidenceRepository
                .findByStudentIdAndSkillId(student.getId(), skillId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Student findStudent(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        return studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student profile not found"
                ));
    }

    private SkillEvidenceResponse toResponse(SkillEvidence evidence) {
        return new SkillEvidenceResponse(
                evidence.getId(),
                evidence.getSkill().getId(),
                evidence.getSkill().getName(),
                evidence.getEvidenceType(),
                evidence.getTitle(),
                evidence.getDescription(),
                evidence.getReferenceUrl(),
                evidence.getEvidenceDate(),
                evidence.getVerificationStatus(),
                evidence.getCreatedAt()
        );
    }
}