package com.malik.lms.quiz.service;

import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.quiz.dto.request.CreateQuizQuestionRequest;
import com.malik.lms.quiz.dto.response.CreateQuizQuestionResponse;
import com.malik.lms.quiz.dto.response.QuizQuestionResponse;
import com.malik.lms.quiz.entity.Quiz;
import com.malik.lms.quiz.entity.QuizQuestion;
import com.malik.lms.quiz.repository.QuizQuestionRepository;
import com.malik.lms.quiz.repository.QuizRepository;
import com.malik.lms.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizQuestionService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    public QuizQuestionService(QuizRepository quizRepository, QuizQuestionRepository quizQuestionRepository) {
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
    }

    @Transactional
    public CreateQuizQuestionResponse createQuestion(Long quizId, CreateQuizQuestionRequest request, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Quiz quiz = quizRepository.findByIdAndCourseInstructorId(quizId, instructorId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        Course course = quiz.getCourse();

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new RuntimeException("Only draft or rejected courses can be edited");
        }

        if (quizQuestionRepository.existsByQuizIdAndDisplayOrder(quizId, request.getDisplayOrder())) {
            throw new RuntimeException("Question display order already exists");
        }

        QuizQuestion question = new QuizQuestion();

        question.setQuiz(quiz);
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectOption(request.getCorrectOption());
        question.setDisplayOrder(request.getDisplayOrder());

        QuizQuestion savedQuestion = quizQuestionRepository.save(question);

        return new CreateQuizQuestionResponse(savedQuestion.getId(), quiz.getId(), savedQuestion.getQuestionText(), savedQuestion.getDisplayOrder(), "Question created successfully");
    }

    public List<QuizQuestionResponse> getQuestions(Long quizId, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Quiz quiz = quizRepository.findByIdAndCourseInstructorId(quizId, instructorId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        return quizQuestionRepository.findByQuizIdOrderByDisplayOrderAsc(quiz.getId())
                .stream()
                .map(question ->
                        new QuizQuestionResponse(question.getId(), question.getQuestionText(), question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD(), question.getCorrectOption(), question.getDisplayOrder()))
                .toList();
    }
}
