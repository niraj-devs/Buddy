package com.buddy.academic.controller;

import com.buddy.academic.dto.BranchResponse;
import com.buddy.academic.dto.CollegeResponse;
import com.buddy.academic.dto.SemesterResponse;
import com.buddy.academic.dto.SubjectResponse;
import com.buddy.academic.dto.TopicResourceResponse;
import com.buddy.academic.dto.TopicResponse;
import com.buddy.academic.dto.UnitResponse;
import com.buddy.academic.service.AcademicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/academic")
public class AcademicController {

    private final AcademicService academicService;

    public AcademicController(AcademicService academicService) {
        this.academicService = academicService;
    }

    @GetMapping("/colleges")
    public List<CollegeResponse> getColleges() {
        return academicService.getColleges();
    }

    @GetMapping("/colleges/{collegeId}/branches")
    public List<BranchResponse> getBranchesByCollege(
            @PathVariable Long collegeId
    ) {
        return academicService.getBranchesByCollege(collegeId);
    }

    @GetMapping("/branches/{branchId}/semesters")
    public List<SemesterResponse> getSemestersByBranch(
            @PathVariable Long branchId
    ) {
        return academicService.getSemestersByBranch(branchId);
    }

    @GetMapping("/semesters/{semesterId}/subjects")
    public List<SubjectResponse> getSubjectsBySemester(
            @PathVariable Long semesterId
    ) {
        return academicService.getSubjectsBySemester(semesterId);
    }

    @GetMapping("/subjects/{subjectId}/units")
    public List<UnitResponse> getUnitsBySubject(
            @PathVariable Long subjectId
    ) {
        return academicService.getUnitsBySubject(subjectId);
    }

    @GetMapping("/units/{unitId}/topics")
    public List<TopicResponse> getTopicsByUnit(
            @PathVariable Long unitId
    ) {
        return academicService.getTopicsByUnit(unitId);
    }


    @GetMapping("/topics/{topicId}/prerequisites")
    public List<TopicResponse> getPrerequisitesByTopic(
            @PathVariable Long topicId
    ) {
        return academicService.getPrerequisitesByTopic(topicId);
    }
    @GetMapping("/topics/{topicId}/resources")
    public List<TopicResourceResponse> getResourcesByTopic(
            @PathVariable Long topicId
    ) {
        return academicService.getResourcesByTopic(topicId);
    }
}
