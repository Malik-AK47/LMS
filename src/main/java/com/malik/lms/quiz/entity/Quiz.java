package com.malik.lms.quiz.entity;

import com.malik.lms.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "quizzes", uniqueConstraints = {@UniqueConstraint(name = "uk_quiz_course", columnNames = "course_id")})
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "course_id", nullable = false, unique = true)
    private Course course;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer passingScore;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
