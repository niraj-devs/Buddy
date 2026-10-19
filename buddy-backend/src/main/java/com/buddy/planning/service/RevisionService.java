package com.buddy.planning.service;

import com.buddy.academic.entity.StudentTopicProgress;
import com.buddy.academic.repository.StudentTopicProgressRepository;
import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.planning.dto.CreateRevisionRequest;
import com.buddy.planning.dto.RevisionResponse;
import com.buddy.planning.entity.Revision;
import com.buddy.planning.repository.RevisionRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RevisionService {

    private static final String SCHEDULED = "SCHEDULED";
    private static final String COMPLETED = "COMPLETED";
    private static final String MISSED = "MISSED";

    private final RevisionRepository revisionRepository;
    private final StudentTopicProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public RevisionService(
            RevisionRepository revisionRepository,
            StudentTopicProgressRepository progressRepository,
            UserRepository userRepository,
            StudentRepository studentRepository
    ) {
        this.revisionRepository = revisionRepository;
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public RevisionResponse scheduleRevision(
            String email,
            CreateRevisionRequest request
    ) {
        Student student = findStudentByEmail(email);

        StudentTopicProgress progress = progressRepository
                .findByStudentIdAndTopicId(
                        student.getId(),
                        request.topicId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Topic progress not found for this student"
                ));

        Revision previousRevision = revisionRepository
                .findTopByProgressIdOrderByRevisionNumberDesc(
                        progress.getId()
                )
                .orElse(null);

        int nextRevisionNumber = previousRevision == null
                ? 1
                : previousRevision.getRevisionNumber() + 1;

        Revision revision = new Revision();
        revision.setProgress(progress);
        revision.setStudent(student);
        revision.setTopic(progress.getTopic());
        revision.setRevisionNumber(nextRevisionNumber);
        revision.setScheduledDate(request.scheduledDate());
        revision.setStatus(SCHEDULED);

        return mapToResponse(revisionRepository.save(revision));
    }

    @Transactional(readOnly = true)
    public List<RevisionResponse> getMyRevisions(String email) {
        Student student = findStudentByEmail(email);

        return revisionRepository.findByStudentId(student.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RevisionResponse> getTodaysRevisions(String email) {
        Student student = findStudentByEmail(email);

        return revisionRepository
                .findByStudentIdAndScheduledDate(
                        student.getId(),
                        LocalDate.now()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RevisionResponse> getUpcomingRevisions(String email) {
        Student student = findStudentByEmail(email);

        return revisionRepository
                .findByStudentIdAndStatusAndScheduledDateGreaterThanEqualOrderByScheduledDateAsc(
                        student.getId(),
                        SCHEDULED,
                        LocalDate.now()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public RevisionResponse completeRevision(
            String email,
            Long revisionId
    ) {
        Student student = findStudentByEmail(email);
        Revision revision = findRevisionForStudent(
                student.getId(),
                revisionId
        );

        ensureScheduled(revision);

        LocalDateTime now = LocalDateTime.now();

        revision.setStatus(COMPLETED);
        revision.setCompletedAt(now);

        StudentTopicProgress progress = revision.getProgress();
        progress.setLastRevisedAt(now);

        progressRepository.save(progress);

        return mapToResponse(revisionRepository.save(revision));
    }

    @Transactional
    public RevisionResponse markRevisionMissed(
            String email,
            Long revisionId
    ) {
        Student student = findStudentByEmail(email);
        Revision revision = findRevisionForStudent(
                student.getId(),
                revisionId
        );

        ensureScheduled(revision);

        revision.setStatus(MISSED);
        revision.setCompletedAt(null);

        return mapToResponse(revisionRepository.save(revision));
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

    private Revision findRevisionForStudent(
            Long studentId,
            Long revisionId
    ) {
        Revision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Revision not found"
                ));

        if (!revision.getStudent().getId().equals(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Revision not found"
            );
        }

        return revision;
    }

    private void ensureScheduled(Revision revision) {
        if (!SCHEDULED.equals(revision.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only scheduled revisions can be completed or marked missed"
            );
        }
    }

    private RevisionResponse mapToResponse(Revision revision) {
        return new RevisionResponse(
                revision.getId(),
                revision.getTopic().getId(),
                revision.getTopic().getTitle(),
                revision.getProgress().getId(),
                revision.getRevisionNumber(),
                revision.getScheduledDate(),
                revision.getStatus(),
                revision.getCompletedAt(),
                revision.getCreatedAt()
        );
    }
}