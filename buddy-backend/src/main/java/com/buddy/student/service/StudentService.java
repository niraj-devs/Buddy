package com.buddy.student.service;

import com.buddy.academic.entity.Branch;
import com.buddy.academic.entity.College;
import com.buddy.academic.entity.Semester;
import com.buddy.academic.repository.BranchRepository;
import com.buddy.academic.repository.CollegeRepository;
import com.buddy.academic.repository.SemesterRepository;
import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.development.entity.CareerPath;
import com.buddy.development.repository.CareerPathRepository;
import com.buddy.student.dto.CreateStudentProfileRequest;
import com.buddy.student.dto.StudentProfileResponse;
import com.buddy.student.dto.UpdateStudentProfileRequest;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;
    private final SemesterRepository semesterRepository;
    private final CareerPathRepository careerPathRepository;

    public StudentService(
            StudentRepository studentRepository,
            UserRepository userRepository,
            CollegeRepository collegeRepository,
            BranchRepository branchRepository,
            SemesterRepository semesterRepository,
            CareerPathRepository careerPathRepository
    ) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
        this.branchRepository = branchRepository;
        this.semesterRepository = semesterRepository;
        this.careerPathRepository = careerPathRepository;
    }

    @Transactional
    public StudentProfileResponse createMyProfile(
            String email,
            CreateStudentProfileRequest request
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Authenticated user was not found"
                ));

        if (studentRepository.findByUserId(user.getId()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student profile already exists"
            );
        }

        String rollNumber = request.rollNumber().trim();

        if (studentRepository.existsByRollNumber(rollNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Roll number is already registered"
            );
        }

        College college = collegeRepository.findById(request.collegeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "College not found"
                ));

        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found"
                ));

        Semester semester = semesterRepository.findById(request.semesterId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Semester not found"
                ));

        CareerPath careerPath = careerPathRepository.findById(
                        request.careerPathId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Career path not found"
                ));

        if (!branch.getCollege().getId().equals(college.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected branch does not belong to the selected college"
            );
        }

        if (!semester.getBranch().getId().equals(branch.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected semester does not belong to the selected branch"
            );
        }

        if (!careerPath.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected career path is inactive"
            );
        }

        Student student = new Student();
        student.setUser(user);
        student.setCollege(college);
        student.setBranch(branch);
        student.setSemester(semester);
        student.setCareerPath(careerPath);
        student.setRollNumber(rollNumber);
        student.setCgpa(request.cgpa());
        student.setGraduationYear(request.graduationYear());

        Student savedStudent = studentRepository.save(student);

        return mapToResponse(savedStudent);
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getMyProfile(String email) {
        Student student = findStudentByEmail(email);
        return mapToResponse(student);
    }

    @Transactional
    public StudentProfileResponse updateMyProfile(
            String email,
            UpdateStudentProfileRequest request
    ) {
        Student student = findStudentByEmail(email);
        User user = student.getUser();

        if (request.firstName() != null) {
            user.setFirstName(request.firstName().trim());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName().trim());
        }

        if (request.cgpa() != null) {
            student.setCgpa(request.cgpa());
        }

        if (request.graduationYear() != null) {
            student.setGraduationYear(request.graduationYear());
        }

        return mapToResponse(student);
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