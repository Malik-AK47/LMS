package com.malik.lms.quiz.repository;

import com.malik.lms.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByCourseId(Long courseId);
    boolean existsByCourseId(Long courseId);

    Optional<Quiz> findByIdAndCourseInstructorId(Long quizId, Long instructorId);

    @Modifying
    @Query("""
        DELETE FROM Quiz q
        WHERE q.course.id = :courseId
        """)
    void deleteByCourseId(@Param("courseId") Long courseId);
}
