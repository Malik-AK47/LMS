package com.malik.lms.quiz.service;

import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.exception.BadRequestException;
import com.malik.lms.exception.ConflictException;
import com.malik.lms.exception.ResourceNotFoundException;
import com.malik.lms.quiz.dto.request.CreateQuizQuestionRequest;
import com.malik.lms.quiz.dto.request.UpdateQuizQuestionRequest;
import com.malik.lms.quiz.dto.response.CreateQuizQuestionResponse;
import com.malik.lms.quiz.dto.response.QuizQuestionResponse;
import com.malik.lms.quiz.dto.response.UpdateQuizQuestionResponse;
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

        Quiz quiz = quizRepository.findByIdAndCourseInstructorId(quizId, instructorId).orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        Course course = quiz.getCourse();

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected courses can be edited");
        }

        if (quizQuestionRepository.existsByQuizIdAndDisplayOrder(quizId, request.getDisplayOrder())) {
            throw new ConflictException("Question display order already exists");
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

        Quiz quiz = quizRepository.findByIdAndCourseInstructorId(quizId, instructorId).orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        return quizQuestionRepository.findByQuizIdOrderByDisplayOrderAsc(quiz.getId())
                .stream()
                .map(question ->
                        new QuizQuestionResponse(question.getId(), question.getQuestionText(), question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD(), question.getCorrectOption(), question.getDisplayOrder()))
                .toList();
    }


    @Transactional
    public UpdateQuizQuestionResponse updateQuestion(Long questionId, UpdateQuizQuestionRequest request, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        QuizQuestion question = quizQuestionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        Quiz quiz = question.getQuiz();
        Course course = quiz.getCourse();

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new ResourceNotFoundException("Question not found");
        }
        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected courses can be edited");
        }

        Integer oldOrder = question.getDisplayOrder();
        Integer newOrder = request.getDisplayOrder();

        if (!oldOrder.equals(newOrder)) {
            List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByDisplayOrderAsc(quiz.getId());

            if (newOrder > questions.size()) {
                throw new BadRequestException("Display order cannot be greater than the number of questions");
            }

            question.setDisplayOrder(-1);
            quizQuestionRepository.saveAndFlush(question);

            if (newOrder < oldOrder) {
                for (QuizQuestion q : questions) {
                    if (!q.getId().equals(questionId) && q.getDisplayOrder() >= newOrder && q.getDisplayOrder() < oldOrder) {
                        q.setDisplayOrder(q.getDisplayOrder() + 1);
                    }
                }
            } else {
                for (QuizQuestion q : questions) {
                    if (!q.getId().equals(questionId) && q.getDisplayOrder() > oldOrder && q.getDisplayOrder() <= newOrder) {
                        q.setDisplayOrder(q.getDisplayOrder() - 1);
                    }
                }
            }

            quizQuestionRepository.saveAll(questions);

            question.setDisplayOrder(newOrder);
        }

        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectOption(request.getCorrectOption());

        QuizQuestion updatedQuestion = quizQuestionRepository.save(question);

        return new UpdateQuizQuestionResponse(updatedQuestion.getId(), updatedQuestion.getQuestionText(), updatedQuestion.getDisplayOrder(), "Question updated successfully");
    }


    @Transactional
    public String deleteQuestion(Long questionId, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        QuizQuestion question = quizQuestionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        Quiz quiz = question.getQuiz();
        Course course = quiz.getCourse();

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new ResourceNotFoundException("Question not found");
        }
        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected courses can be edited");
        }

        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByDisplayOrderAsc(quiz.getId());

        int temporaryOrder = -1;

        for (QuizQuestion q : questions) {
            q.setDisplayOrder(temporaryOrder--);
        }

        quizQuestionRepository.saveAllAndFlush(questions);

        questions.removeIf(q -> q.getId().equals(questionId));

        for (int i = 0; i < questions.size(); i++) {
            questions.get(i).setDisplayOrder(i + 1);
        }

        quizQuestionRepository.delete(question);
        quizQuestionRepository.saveAllAndFlush(questions);

        return "Question deleted successfully";
    }
}
