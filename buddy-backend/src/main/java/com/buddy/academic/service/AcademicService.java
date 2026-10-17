package com.buddy.academic.service;

import com.buddy.academic.dto.BranchResponse;
import com.buddy.academic.dto.CollegeResponse;
import com.buddy.academic.dto.SemesterResponse;
import com.buddy.academic.dto.SubjectResponse;
import com.buddy.academic.dto.TopicResourceResponse;
import com.buddy.academic.dto.TopicResponse;
import com.buddy.academic.dto.UnitResponse;
import com.buddy.academic.repository.BranchRepository;
import com.buddy.academic.repository.CollegeRepository;
import com.buddy.academic.repository.SemesterRepository;
import com.buddy.academic.repository.SubjectRepository;
import com.buddy.academic.repository.TopicRepository;
import com.buddy.academic.repository.TopicResourceRepository;
import com.buddy.academic.repository.UnitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AcademicService {

    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final UnitRepository unitRepository;
    private final TopicRepository topicRepository;
    private final TopicResourceRepository topicResourceRepository;

    public AcademicService(
            CollegeRepository collegeRepository,
            BranchRepository branchRepository,
            SemesterRepository semesterRepository,
            SubjectRepository subjectRepository,
            UnitRepository unitRepository,
            TopicRepository topicRepository,
            TopicResourceRepository topicResourceRepository
    ) {
        this.collegeRepository = collegeRepository;
        this.branchRepository = branchRepository;
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
        this.unitRepository = unitRepository;
        this.topicRepository = topicRepository;
        this.topicResourceRepository = topicResourceRepository;
    }

    @Transactional(readOnly = true)
    public List<CollegeResponse> getColleges() {
        return collegeRepository.findAll()
                .stream()
                .map(college -> new CollegeResponse(
                        college.getId(),
                        college.getName(),
                        college.getCity(),
                        college.getState()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getBranchesByCollege(Long collegeId) {
        if (!collegeRepository.existsById(collegeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "College not found"
            );
        }

        return branchRepository.findByCollegeId(collegeId)
                .stream()
                .map(branch -> new BranchResponse(
                        branch.getId(),
                        branch.getName(),
                        branch.getCode()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SemesterResponse> getSemestersByBranch(Long branchId) {
        if (!branchRepository.existsById(branchId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Branch not found"
            );
        }

        return semesterRepository
                .findByBranchIdOrderBySemesterNumber(branchId)
                .stream()
                .map(semester -> new SemesterResponse(
                        semester.getId(),
                        semester.getSemesterNumber()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjectsBySemester(Long semesterId) {
        if (!semesterRepository.existsById(semesterId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Semester not found"
            );
        }

        return subjectRepository.findBySemesterId(semesterId)
                .stream()
                .map(subject -> new SubjectResponse(
                        subject.getId(),
                        subject.getName(),
                        subject.getCode(),
                        subject.getCredits()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UnitResponse> getUnitsBySubject(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Subject not found"
            );
        }

        return unitRepository.findBySubjectIdOrderByUnitNumber(subjectId)
                .stream()
                .map(unit -> new UnitResponse(
                        unit.getId(),
                        unit.getUnitNumber(),
                        unit.getTitle()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopicResponse> getTopicsByUnit(Long unitId) {
        if (!unitRepository.existsById(unitId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Unit not found"
            );
        }

        return topicRepository.findByUnitIdOrderBySequenceNumber(unitId)
                .stream()
                .map(topic -> new TopicResponse(
                        topic.getId(),
                        topic.getTitle(),
                        topic.getDescription(),
                        topic.getSequenceNumber()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopicResourceResponse> getResourcesByTopic(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Topic not found"
            );
        }

        return topicResourceRepository.findByTopicId(topicId)
                .stream()
                .map(resource -> new TopicResourceResponse(
                        resource.getId(),
                        resource.getResourceType(),
                        resource.getTitle(),
                        resource.getUrl(),
                        resource.getDescription(),
                        resource.getCreatedAt()
                ))
                .toList();
    }
}
