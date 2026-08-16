package com.malik.lms.lesson.repository;

import com.malik.lms.lesson.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    boolean existsByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    long countByEnrollmentId(Long enrollmentId);

    @Modifying
    @Query("""
        DELETE FROM LessonProgress lp
        WHERE lp.enrollment.course.id = :courseId
        """)
    void deleteByCourseId(@Param("courseId") Long courseId);
}
