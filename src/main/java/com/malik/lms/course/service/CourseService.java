package com.malik.lms.course.service;

import com.malik.lms.category.entity.Category;
import com.malik.lms.category.repository.CategoryRepository;
import com.malik.lms.certificate.entity.Certificate;
import com.malik.lms.certificate.repository.CertificateRepository;
import com.malik.lms.course.dto.request.CreateCourseRequest;
import com.malik.lms.course.dto.request.RejectCourseRequest;
import com.malik.lms.course.dto.request.UpdateCourseRequest;
import com.malik.lms.course.dto.response.*;
import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.course.repository.CourseRepository;
import com.malik.lms.exception.BadRequestException;
import com.malik.lms.exception.ResourceNotFoundException;
import com.malik.lms.quiz.entity.Quiz;
import com.malik.lms.quiz.repository.QuizQuestionRepository;
import com.malik.lms.quiz.repository.QuizRepository;
import com.malik.lms.section.entity.Section;
import com.malik.lms.security.user.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final CertificateRepository certificateRepository;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository, QuizRepository quizRepository, QuizQuestionRepository quizQuestionRepository, CertificateRepository certificateRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.certificateRepository = certificateRepository;
    }

    public CreateCourseResponse createCourse(CreateCourseRequest createCourseRequest, Authentication authentication) {

        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Category category = categoryRepository.findById(createCourseRequest.getCategoryId()).orElseThrow(()-> new ResourceNotFoundException("category not found..."));

        Course course = new Course();
        course.setTitle(createCourseRequest.getTitle());
        course.setDescription(createCourseRequest.getDescription());
        course.setPrice(createCourseRequest.getPrice());
        course.setDifficultyLevel(createCourseRequest.getDifficultyLevel());
        course.setCategory(category);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        course.setStatus(CourseStatus.DRAFT);
        course.setThumbnail(createCourseRequest.getThumbnail());
        course.setInstructor(instructor.getUser());

        Course savedCourse = courseRepository.save(course);
        return new CreateCourseResponse(savedCourse.getId(), savedCourse.getTitle(), savedCourse.getStatus());
    }

    public Page<CourseSummaryResponse> getPublishedCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable);
        return courses.map(course -> new CourseSummaryResponse(course.getId(), course.getTitle(), course.getPrice(), course.getDifficultyLevel(), course.getThumbnail(), course.getCategory().getName()));
    }

    public CourseDetailsResponse getCourseDetails(Long id) {
        Course course = courseRepository.findByIdAndStatus(id, CourseStatus.PUBLISHED).orElseThrow(()-> new ResourceNotFoundException("course not found..."));
        return new CourseDetailsResponse(course.getId(), course.getTitle(), course.getDescription(), course.getPrice(), course.getDifficultyLevel(), course.getCategory().getName(), course.getInstructor().getFullName(), course.getThumbnail());
    }

    public Page<InstructorCourseSummaryResponse> getAllInstructorCourses(Pageable pageable, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructId = instructor.getUser().getId();

        Page<Course> course = courseRepository.findByInstructorId(instructId, pageable);
        return course.map(instCourses ->
                new InstructorCourseSummaryResponse(instCourses.getId(), instCourses.getTitle(), instCourses.getPrice(), instCourses.getDifficultyLevel(), instCourses.getCategory().getName(), instCourses.getStatus(), instCourses.getCreatedAt(), instCourses.getThumbnail()));
    }

    @Transactional
    public UpdateCourseResponse updateCourse(UpdateCourseRequest updateCourseRequest, Long id, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Course course = courseRepository.findByIdAndInstructorId(id, instructorId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected courses can be edited");
        }

        Category category = categoryRepository.findById(updateCourseRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        course.setTitle(updateCourseRequest.getTitle());
        course.setDescription(updateCourseRequest.getDescription());
        course.setPrice(updateCourseRequest.getPrice());
        course.setDifficultyLevel(updateCourseRequest.getDifficultyLevel());
        course.setCategory(category);
        course.setThumbnail(updateCourseRequest.getThumbnail());
        course.setUpdatedAt(LocalDateTime.now());

        course.setRejectionReason(null);

        Course updatedCourse = courseRepository.save(course);

        return new UpdateCourseResponse(updatedCourse.getId(), updatedCourse.getTitle(), "Course updated successfully");
    }

    @Transactional
    public CourseStatusResponse submitForReview(Long courseId, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Course course = courseRepository.findByIdAndInstructorId(courseId, instructorId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected courses can be submitted");
        }
        if (course.getSections() == null || course.getSections().isEmpty()) {
            throw new BadRequestException("Course must contain at least one section");
        }

        boolean hasLessons = false;
        for (Section section : course.getSections()) {
            if (section.getLessons() != null && !section.getLessons().isEmpty()) {
                hasLessons = true;
                break;
            }
        }
        if (!hasLessons) {
            throw new BadRequestException("Course must contain at least one lesson");
        }

        Quiz quiz = quizRepository.findByCourseId(courseId).orElseThrow(() -> new ResourceNotFoundException("Course must have a final quiz"));
        long questionCount = quizQuestionRepository.countByQuizId(quiz.getId());

        if (questionCount == 0) {
            throw new BadRequestException("Quiz must contain at least one question");
        }

        Certificate certificate = certificateRepository.findByCourseId(courseId).orElseThrow(() -> new ResourceNotFoundException("Course must have a certificate"));

        course.setStatus(CourseStatus.PENDING_REVIEW);
        course.setUpdatedAt(LocalDateTime.now());

        Course savedCourse = courseRepository.save(course);

        return new CourseStatusResponse(savedCourse.getId(), savedCourse.getStatus(), "Course submitted for review");
    }


    public Page<AdminCourseReviewResponse> getPendingCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findByStatus(CourseStatus.PENDING_REVIEW, pageable);

        return courses.map(course -> new AdminCourseReviewResponse(course.getId(), course.getTitle(), course.getPrice(), course.getDifficultyLevel(), course.getCategory().getName(), course.getInstructor().getFullName(), course.getStatus(), course.getCreatedAt()));
    }

    @Transactional
    public CourseStatusResponse publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getStatus() != CourseStatus.PENDING_REVIEW) {
            throw new BadRequestException("Only courses pending review can be published");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        course.setUpdatedAt(LocalDateTime.now());

        Course savedCourse = courseRepository.save(course);

        return new CourseStatusResponse(savedCourse.getId(), savedCourse.getStatus(), "Course published successfully");
    }


    @Transactional
    public CourseStatusResponse rejectCourse(Long courseId, RejectCourseRequest request) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getStatus() != CourseStatus.PENDING_REVIEW) {
            throw new BadRequestException("Only courses pending review can be rejected");
        }

        course.setStatus(CourseStatus.REJECTED);
        course.setRejectionReason(request.getReason());
        course.setUpdatedAt(LocalDateTime.now());

        Course savedCourse = courseRepository.save(course);

        return new CourseStatusResponse(savedCourse.getId(), savedCourse.getStatus(), savedCourse.getRejectionReason());
    }
}
