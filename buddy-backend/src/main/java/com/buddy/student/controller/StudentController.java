package com.buddy.student.controller;

import com.buddy.student.dto.CreateStudentProfileRequest;
import com.buddy.student.dto.StudentProfileResponse;
import com.buddy.student.dto.UpdateStudentProfileRequest;
import com.buddy.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public StudentProfileResponse createMyProfile(
            Authentication authentication,
            @Valid @RequestBody CreateStudentProfileRequest request
    ) {
        return studentService.createMyProfile(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/me")
    public StudentProfileResponse getMyProfile(
            Authentication authentication
    ) {
        return studentService.getMyProfile(authentication.getName());
    }

    @PatchMapping("/me")
    public StudentProfileResponse updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateStudentProfileRequest request
    ) {
        return studentService.updateMyProfile(
                authentication.getName(),
                request
        );
    }
}