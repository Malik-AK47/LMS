package com.malik.lms.user.controller;

import com.malik.lms.user.dto.request.CreateInstructorRequest;
import com.malik.lms.user.dto.request.InstructorStatusRequest;
import com.malik.lms.user.dto.response.InstructorResponse;
import com.malik.lms.user.service.InstructorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/instructors")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public InstructorResponse createInstructor(@Valid @RequestBody CreateInstructorRequest request) {
        return instructorService.createInstructor(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<InstructorResponse> getInstructors(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return instructorService.getInstructors(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{instructorId}")
    public InstructorResponse getInstructor(@PathVariable Long instructorId) {
        return instructorService.getInstructor(instructorId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{instructorId}/status")
    public InstructorResponse updateInstructorStatus(@PathVariable Long instructorId, @Valid @RequestBody InstructorStatusRequest request) {
        return instructorService.updateInstructorStatus(instructorId, request);
    }
}