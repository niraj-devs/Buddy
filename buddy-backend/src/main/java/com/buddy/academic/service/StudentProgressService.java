package com.buddy.academic.service;

import com.buddy.academic.dto.progress.TopicProgressResponse;
import com.buddy.academic.dto.progress.TopicUnlockResponse;
import com.buddy.academic.entity.StudentTopicProgress;
import com.buddy.academic.entity.Topic;
import com.buddy.academic.repository.StudentTopicProgressRepository;
import com.buddy.academic.repository.TopicRepository;
import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentProgressService {

    private static final String NOT_STARTED = "NOT_STARTED";
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String COMPLETED = "COMPLETED";

    private final StudentTopicProgressRepository progressRepository;
    private final TopicRepository topicRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public StudentProgressService(
            StudentTopicProgressRepository progressRepository,
            TopicRepository topicRepository,
            StudentRepository studentRepository,
            UserRepository userRepository
    ) {
        this.progressRepository = progressRepository;
        this.topicRepository = topicRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TopicProgressResponse startTopic(String email, Long topicId) {
        Student student = findStudentByEmail(email);
        Topic topic = findTopicById(topicId);

        ensurePrerequisitesCompleted(student, topic);

        StudentTopicProgress progress = progressRepository
                .findByStudentIdAndTopicId(student.getId(), topicId)
                .orElseGet(() -> {
                    StudentTopicProgress newProgress =
                            new StudentTopicProgress();
                    newProgress.setStudent(student);
                    newProgress.setTopic(topic);
                    newProgress.setStatus(NOT_STARTED);
                    newProgress.setProgressPercentage(BigDecimal.ZERO);
                    return newProgress;
                });

        if (COMPLETED.equals(progress.getStatus())) {
            return mapToResponse(progress);
        }

        LocalDateTime now = LocalDateTime.now();

        if (progress.getStartedAt() == null) {
            progress.setStartedAt(now);
        }

        progress.setStatus(IN_PROGRESS);

        if (progress.getProgressPercentage() == null) {
            progress.setProgressPercentage(BigDecimal.ZERO);
        }

        return mapToResponse(progressRepository.save(progress));
    }

    @Transactional
    public TopicProgressResponse updateProgress(
            String email,
            Long topicId,
            BigDecimal percentage
    ) {
        if (percentage == null
                || percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Progress percentage must be between 0 and 100"
            );
        }

        Student student = findStudentByEmail(email);
        Topic topic = findTopicById(topicId);

        ensurePrerequisitesCompleted(student, topic);

        StudentTopicProgress progress = findOrCreateProgress(student, topic);

        if (COMPLETED.equals(progress.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Completed topic progress cannot be changed"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (progress.getStartedAt() == null) {
            progress.setStartedAt(now);
        }

        if (percentage.compareTo(new BigDecimal("100")) == 0) {
            progress.setStatus(COMPLETED);
            progress.setProgressPercentage(new BigDecimal("100.00"));
            progress.setCompletedAt(now);
        } else {
            progress.setStatus(IN_PROGRESS);
            progress.setProgressPercentage(percentage);
            progress.setCompletedAt(null);
        }

        return mapToResponse(progressRepository.save(progress));
    }

    @Transactional
    public TopicProgressResponse completeTopic(String email, Long topicId) {
        Student student = findStudentByEmail(email);
        Topic topic = findTopicById(topicId);

        ensurePrerequisitesCompleted(student, topic);

        StudentTopicProgress progress = findOrCreateProgress(student, topic);

        if (!COMPLETED.equals(progress.getStatus())) {
            LocalDateTime now = LocalDateTime.now();

            if (progress.getStartedAt() == null) {
                progress.setStartedAt(now);
            }

            progress.setStatus(COMPLETED);
            progress.setProgressPercentage(new BigDecimal("100.00"));
            progress.setCompletedAt(now);
        }

        return mapToResponse(progressRepository.save(progress));
    }

    @Transactional(readOnly = true)
    public List<TopicProgressResponse> getMyProgress(String email) {
        Student student = findStudentByEmail(email);

        return progressRepository.findByStudentId(student.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopicUnlockResponse> getUnlockedTopics(String email) {
        Student student = findStudentByEmail(email);

        Map<Long, StudentTopicProgress> progressByTopicId = new HashMap<>();

        for (StudentTopicProgress progress :
                progressRepository.findByStudentId(student.getId())) {
            progressByTopicId.put(
                    progress.getTopic().getId(),
                    progress
            );
        }

        return topicRepository.findAll()
                .stream()
                .map(topic -> {
                    StudentTopicProgress progress =
                            progressByTopicId.get(topic.getId());

                    String status = progress == null
                            ? NOT_STARTED
                            : progress.getStatus();

                    BigDecimal percentage = progress == null
                            ? BigDecimal.ZERO
                            : progress.getProgressPercentage();

                    List<String> incompletePrerequisites =
                            topic.getPrerequisites()
                                    .stream()
                                    .filter(prerequisite -> {
                                        StudentTopicProgress prerequisiteProgress =
                                                progressByTopicId.get(
                                                        prerequisite.getId()
                                                );

                                        return prerequisiteProgress == null
                                                || !COMPLETED.equals(
                                                        prerequisiteProgress.getStatus()
                                                );
                                    })
                                    .map(Topic::getTitle)
                                    .sorted()
                                    .toList();

                    return new TopicUnlockResponse(
                            topic.getId(),
                            topic.getTitle(),
                            status,
                            percentage,
                            incompletePrerequisites.isEmpty(),
                            incompletePrerequisites
                    );
                })
                .toList();
    }

    private StudentTopicProgress findOrCreateProgress(
            Student student,
            Topic topic
    ) {
        return progressRepository
                .findByStudentIdAndTopicId(student.getId(), topic.getId())
                .orElseGet(() -> {
                    StudentTopicProgress progress =
                            new StudentTopicProgress();
                    progress.setStudent(student);
                    progress.setTopic(topic);
                    progress.setStatus(NOT_STARTED);
                    progress.setProgressPercentage(BigDecimal.ZERO);
                    return progress;
                });
    }

    private void ensurePrerequisitesCompleted(
            Student student,
            Topic topic
    ) {
        for (Topic prerequisite : topic.getPrerequisites()) {
            StudentTopicProgress prerequisiteProgress = progressRepository
                    .findByStudentIdAndTopicId(
                            student.getId(),
                            prerequisite.getId()
                    )
                    .orElse(null);

            if (prerequisiteProgress == null
                    || !COMPLETED.equals(prerequisiteProgress.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Complete prerequisite topic '"
                                + prerequisite.getTitle()
                                + "' before starting or completing this topic"
                );
            }
        }
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

    private Topic findTopicById(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Topic not found"
                ));
    }

    private TopicProgressResponse mapToResponse(
            StudentTopicProgress progress
    ) {
        return new TopicProgressResponse(
                progress.getTopic().getId(),
                progress.getTopic().getTitle(),
                progress.getStatus(),
                progress.getProgressPercentage(),
                progress.getStartedAt(),
                progress.getCompletedAt(),
                progress.getLastRevisedAt(),
                progress.getUpdatedAt()
        );
    }
}