package com.malik.lms.enrollment.repository;

import com.malik.lms.enrollment.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Page<Enrollment> findByUserId(Long userId, Pageable pageable);

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}
