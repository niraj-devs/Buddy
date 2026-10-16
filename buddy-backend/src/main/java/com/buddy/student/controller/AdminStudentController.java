package com.buddy.student.controller;

import com.buddy.student.dto.StudentProfileResponse;
import com.buddy.student.service.AdminStudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/students")
public class AdminStudentController {

    private final AdminStudentService adminStudentService;

    public AdminStudentController(AdminStudentService adminStudentService) {
        this.adminStudentService = adminStudentService;
    }

    @GetMapping
    public Page<StudentProfileResponse> getAllStudents(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return adminStudentService.getAllStudents(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        adminStudentService.deleteStudent(id);
    }
}
