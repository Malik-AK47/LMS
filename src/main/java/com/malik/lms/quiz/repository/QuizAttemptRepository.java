package com.malik.lms.quiz.repository;

import com.malik.lms.quiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    long countByEnrollmentIdAndQuizId(Long enrollmentId, Long quizId);

    List<QuizAttempt> findByEnrollmentIdAndQuizIdOrderByAttemptNumberAsc(Long enrollmentId, Long quizId);

    Optional<QuizAttempt> findFirstByEnrollmentIdAndQuizIdAndPassedTrueOrderBySubmittedAtDesc(Long enrollmentId, Long quizId);
}
