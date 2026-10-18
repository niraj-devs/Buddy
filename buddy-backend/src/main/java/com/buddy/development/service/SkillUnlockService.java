package com.buddy.development.service;

import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.development.dto.SkillUnlockResponse;
import com.buddy.development.entity.Skill;
import com.buddy.development.entity.SkillPrerequisite;
import com.buddy.development.entity.StudentSkill;
import com.buddy.development.repository.SkillPrerequisiteRepository;
import com.buddy.development.repository.SkillRepository;
import com.buddy.development.repository.StudentSkillRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkillUnlockService {

    private static final String COMPLETED = "COMPLETED";

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final SkillPrerequisiteRepository prerequisiteRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final SkillRepository skillRepository;

    public SkillUnlockService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            SkillPrerequisiteRepository prerequisiteRepository,
            StudentSkillRepository studentSkillRepository,
            SkillRepository skillRepository
    ) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public SkillUnlockResponse getSkillUnlockStatus(
            String email,
            Long skillId
    ) {
        Student student = findStudentByEmail(email);

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Skill not found"
                ));

        List<SkillPrerequisite> prerequisites =
                prerequisiteRepository.findBySkillId(skillId);

        Map<Long, StudentSkill> progressBySkillId =
                getProgressBySkillId(student.getId());

        List<String> unmetPrerequisites = prerequisites.stream()
                .filter(prerequisite -> {
                    Long prerequisiteId =
                            prerequisite.getPrerequisiteSkill().getId();

                    StudentSkill progress =
                            progressBySkillId.get(prerequisiteId);

                    return progress == null
                            || !COMPLETED.equals(progress.getStatus());
                })
                .map(prerequisite ->
                        prerequisite.getPrerequisiteSkill().getName())
                .toList();

        return new SkillUnlockResponse(
                skill.getId(),
                skill.getName(),
                unmetPrerequisites.isEmpty(),
                unmetPrerequisites
        );
    }

    @Transactional(readOnly = true)
    public boolean isSkillUnlocked(
            Long studentId,
            Long skillId
    ) {
        Map<Long, StudentSkill> progressBySkillId =
                getProgressBySkillId(studentId);

        return prerequisiteRepository.findBySkillId(skillId)
                .stream()
                .allMatch(prerequisite -> {
                    StudentSkill progress = progressBySkillId.get(
                            prerequisite.getPrerequisiteSkill().getId()
                    );

                    return progress != null
                            && COMPLETED.equals(progress.getStatus());
                });
    }

    private Map<Long, StudentSkill> getProgressBySkillId(Long studentId) {
        Map<Long, StudentSkill> progressBySkillId = new HashMap<>();

        for (StudentSkill progress :
                studentSkillRepository.findByStudentId(studentId)) {
            progressBySkillId.put(
                    progress.getSkill().getId(),
                    progress
            );
        }

        return progressBySkillId;
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
}