package com.malik.lms.user.service;

import com.malik.lms.exception.BadRequestException;
import com.malik.lms.exception.ConflictException;
import com.malik.lms.exception.ResourceNotFoundException;
import com.malik.lms.user.dto.request.CreateInstructorRequest;
import com.malik.lms.user.dto.request.InstructorStatusRequest;
import com.malik.lms.user.dto.response.InstructorResponse;
import com.malik.lms.user.entity.User;
import com.malik.lms.user.enums.RoleType;
import com.malik.lms.user.enums.UserStatus;
import com.malik.lms.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InstructorService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InstructorService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public InstructorResponse createInstructor(CreateInstructorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        User instructor = new User();
        instructor.setFullName(request.getFullName().trim());
        instructor.setEmail(request.getEmail().trim().toLowerCase());
        instructor.setPassword(passwordEncoder.encode(request.getPassword()));
        instructor.setRole(RoleType.INSTRUCTOR);
        instructor.setStatus(UserStatus.ACTIVE);
        instructor.setEmailVerified(true);
        instructor.setCreatedAt(LocalDateTime.now());
        instructor.setUpdatedAt(LocalDateTime.now());

        User savedInstructor = userRepository.save(instructor);

        return toResponse(savedInstructor);
    }

    public Page<InstructorResponse> getInstructors(Pageable pageable) {
        return userRepository.findByRole(RoleType.INSTRUCTOR, pageable).map(this::toResponse);
    }

    public InstructorResponse getInstructor(Long instructorId) {
        User instructor = getInstructorUser(instructorId);
        return toResponse(instructor);
    }

    @Transactional
    public InstructorResponse updateInstructorStatus(Long instructorId, InstructorStatusRequest request) {
        User instructor = getInstructorUser(instructorId);

        if (instructor.getStatus() == request.getStatus()) {
            throw new BadRequestException("Instructor already has this status");
        }

        instructor.setStatus(request.getStatus());
        instructor.setUpdatedAt(LocalDateTime.now());

        User updatedInstructor = userRepository.save(instructor);

        return toResponse(updatedInstructor);
    }

    private User getInstructorUser(Long instructorId) {
        User instructor = userRepository.findById(instructorId).orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        if (instructor.getRole() != RoleType.INSTRUCTOR) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        return instructor;
    }

    // Repeated dto response
    private InstructorResponse toResponse(User instructor) {
        return new InstructorResponse(instructor.getId(), instructor.getFullName(), instructor.getEmail(), instructor.getRole(), instructor.getStatus(), instructor.isEmailVerified(), instructor.getCreatedAt());
    }
}