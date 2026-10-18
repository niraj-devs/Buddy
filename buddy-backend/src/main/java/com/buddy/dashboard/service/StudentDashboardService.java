package com.buddy.dashboard.service;

import com.buddy.academic.entity.StudentTopicProgress;
import com.buddy.academic.repository.StudentTopicProgressRepository;
import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.dashboard.dto.StudentDashboardResponse;
import com.buddy.development.entity.SkillEvidence;
import com.buddy.development.entity.StudentSkill;
import com.buddy.development.repository.SkillEvidenceRepository;
import com.buddy.development.repository.StudentSkillRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StudentDashboardService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentTopicProgressRepository topicProgressRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final SkillEvidenceRepository evidenceRepository;

    public StudentDashboardService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            StudentTopicProgressRepository topicProgressRepository,
            StudentSkillRepository studentSkillRepository,
            SkillEvidenceRepository evidenceRepository
    ) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.topicProgressRepository = topicProgressRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.evidenceRepository = evidenceRepository;
    }

    @Transactional(readOnly = true)
    public StudentDashboardResponse getMyDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Authenticated user was not found"
                ));

        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile has not been created"
                ));

        List<StudentTopicProgress> topics =
                topicProgressRepository.findByStudentId(student.getId());

        List<StudentSkill> skills =
                studentSkillRepository.findByStudentId(student.getId());

        List<SkillEvidence> evidence =
                evidenceRepository.findByStudentId(student.getId());

        return new StudentDashboardResponse(
                topics.size(),
                countStatus(topics.stream()
                        .map(StudentTopicProgress::getStatus).toList(),
                        "COMPLETED"),
                countStatus(topics.stream()
                        .map(StudentTopicProgress::getStatus).toList(),
                        "IN_PROGRESS"),
                skills.size(),
                countStatus(skills.stream()
                        .map(StudentSkill::getStatus).toList(),
                        "COMPLETED"),
                countStatus(skills.stream()
                        .map(StudentSkill::getStatus).toList(),
                        "IN_PROGRESS"),
                evidence.size(),
                (int) evidence.stream()
                        .filter(item -> "PENDING".equals(
                                item.getVerificationStatus()))
                        .count()
        );
    }

    private int countStatus(List<String> statuses, String target) {
        return (int) statuses.stream()
                .filter(target::equals)
                .count();
    }
}