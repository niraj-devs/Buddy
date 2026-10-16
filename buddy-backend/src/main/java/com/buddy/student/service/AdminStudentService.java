package com.buddy.student.service;

import com.buddy.student.dto.StudentProfileResponse;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminStudentService {

    private final StudentRepository studentRepository;

    public AdminStudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public Page<StudentProfileResponse> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile not found"
                ));

        studentRepository.delete(student);
        studentRepository.flush();
    }

    private StudentProfileResponse mapToResponse(Student student) {
        return new StudentProfileResponse(
                student.getId(),
                student.getUser().getFirstName(),
                student.getUser().getLastName(),
                student.getUser().getEmail(),
                student.getCollege().getName(),
                student.getCollege().getCity(),
                student.getCollege().getState(),
                student.getBranch().getName(),
                student.getBranch().getCode(),
                student.getSemester().getSemesterNumber(),
                student.getCareerPath().getName(),
                student.getRollNumber(),
                student.getCgpa(),
                student.getGraduationYear(),
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }
}
