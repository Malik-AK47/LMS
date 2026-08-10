package com.malik.lms.course.repository;

import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByStatus(CourseStatus published, Pageable pageable);

    Optional<Course> findByIdAndStatus(Long id, CourseStatus courseStatus);

    Page<Course> findByInstructorId(Long instructId, Pageable pageable);

    Optional<Course> findByIdAndInstructorId(Long id, Long instructorId);

}
