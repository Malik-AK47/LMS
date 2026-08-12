package com.malik.lms.quiz.repository;

import com.malik.lms.quiz.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    boolean existsByQuizIdAndDisplayOrder(Long quizId, Integer displayOrder);

    long countByQuizId(Long quizId);

    List<QuizQuestion> findByQuizIdOrderByDisplayOrderAsc(Long quizId);
}
