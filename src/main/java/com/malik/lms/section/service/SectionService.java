package com.malik.lms.section.service;

import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.course.repository.CourseRepository;
import com.malik.lms.enrollment.repository.EnrollmentRepository;
import com.malik.lms.exception.BadRequestException;
import com.malik.lms.exception.ConflictException;
import com.malik.lms.exception.ForbiddenException;
import com.malik.lms.exception.ResourceNotFoundException;
import com.malik.lms.lesson.repository.LessonProgressRepository;
import com.malik.lms.lesson.repository.LessonRepository;
import com.malik.lms.section.dto.request.CreateSectionRequest;
import com.malik.lms.section.dto.request.UpdateSectionRequest;
import com.malik.lms.section.dto.response.CreateSectionResponse;
import com.malik.lms.section.dto.response.SectionSummaryResponse;
import com.malik.lms.section.dto.response.UpdateSectionResponse;
import com.malik.lms.section.entity.Section;
import com.malik.lms.section.repository.SectionRepository;
import com.malik.lms.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;

    public SectionService(SectionRepository sectionRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, LessonRepository lessonRepository, LessonProgressRepository lessonProgressRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
    }

    @Transactional
    public CreateSectionResponse createSection(Long courseId, CreateSectionRequest createSectionRequest, Authentication authentication) {

        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Course course = courseRepository.findByIdAndInstructorId(courseId, instructorId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BadRequestException("Course cannot be modified in its current status");
        }

        if (sectionRepository.existsByCourseIdAndDisplayOrder(courseId, createSectionRequest.getDisplayOrder())) {
            throw new ConflictException("Section order already exists for this course");
        }

        Section section = new Section();

        section.setTitle(createSectionRequest.getTitle());
        section.setDescription(createSectionRequest.getDescription());
        section.setDisplayOrder(createSectionRequest.getDisplayOrder());
        section.setCourse(course);
        section.setCreatedAt(LocalDateTime.now());
        section.setUpdatedAt(LocalDateTime.now());

        Section savedSection = sectionRepository.save(section);

        return new CreateSectionResponse(savedSection.getId(), savedSection.getTitle(), savedSection.getDisplayOrder(), course.getId());
    }

    public List<SectionSummaryResponse> getSections(Long courseId, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Course course = courseRepository.findByIdAndInstructorId(courseId, instructorId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        List<Section> section = course.getSections();

        return section.stream()
                .sorted(Comparator.comparing(Section::getDisplayOrder))
                .map(section1 -> new SectionSummaryResponse(section1.getId(), section1.getTitle(), section1.getDescription(), section1.getDisplayOrder()))
                .toList();
    }

    @Transactional
    public UpdateSectionResponse updateSection(UpdateSectionRequest updateSectionRequest, Long sectionId, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Section section = sectionRepository.findByIdAndCourseInstructorId(sectionId, instructorId).orElseThrow(()-> new ResourceNotFoundException("section not found..."));
        Course course = section.getCourse();

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected courses can be edited");
        }

        Integer oldOrder = section.getDisplayOrder();
        Integer newOrder = updateSectionRequest.getDisplayOrder();

        Long courseId = section.getCourse().getId();

        if (!oldOrder.equals(newOrder)) {
            section.setDisplayOrder(-1);
            sectionRepository.saveAndFlush(section);

            if (newOrder < oldOrder) {
                sectionRepository.shiftSectionsDown(courseId, newOrder, oldOrder);
            } else {
                sectionRepository.shiftSectionsUp(courseId, oldOrder, newOrder);
            }
            section.setDisplayOrder(newOrder);
        }

        section.setTitle(updateSectionRequest.getTitle());
        section.setDescription(updateSectionRequest.getDescription());
        section.setUpdatedAt(LocalDateTime.now());

        Section updatedSection = sectionRepository.save(section);
        return new UpdateSectionResponse(updatedSection.getTitle(), updatedSection.getDescription(), updatedSection.getDisplayOrder());
    }

    public List<SectionSummaryResponse> getStudentSections(Long courseId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BadRequestException("Course is not available");
        }

        if (!enrollmentRepository.existsByUserIdAndCourseId(studentId, courseId)) {
            throw new ResourceNotFoundException("You are not enrolled in this course");
        }

        return course.getSections()
                .stream()
                .sorted(Comparator.comparing(Section::getDisplayOrder))
                .map(section ->
                        new SectionSummaryResponse(section.getId(), section.getTitle(), section.getDescription(), section.getDisplayOrder()))
                .toList();
    }

    @Transactional
    public String deleteSection(Long sectionId, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Section section = sectionRepository.findByIdAndCourseInstructorId(sectionId, instructorId).orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        Course course = section.getCourse();

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected courses can be edited");
        }
        Long courseId = course.getId();
        Integer deletedOrder = section.getDisplayOrder();

        lessonProgressRepository.deleteBySectionId(sectionId);
        lessonRepository.deleteBySectionId(sectionId);

        section.setDisplayOrder(-1);
        sectionRepository.saveAndFlush(section);

        List<Section> sections = sectionRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);

        for (Section remainingSection : sections) {
            if (!remainingSection.getId().equals(sectionId) && remainingSection.getDisplayOrder() > deletedOrder) {
                remainingSection.setDisplayOrder(remainingSection.getDisplayOrder() - 1);
            }
        }

        sectionRepository.saveAll(sections);
        sectionRepository.delete(section);

        return "Section deleted successfully";
    }
}
