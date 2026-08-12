package com.malik.lms.quiz.repository;

import com.malik.lms.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByCourseId(Long courseId);
    boolean existsByCourseId(Long courseId);

    Optional<Quiz> findByIdAndCourseInstructorId(Long quizId, Long instructorId);

}
