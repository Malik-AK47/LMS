package com.malik.lms.lesson.entity;

import com.malik.lms.section.entity.Section;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lessons", uniqueConstraints = { @UniqueConstraint(name = "uk_lesson_section_order", columnNames = {"section_id", "display_order"})})
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private Integer durationInMinutes;

    @Column(nullable = false)
    private Integer displayOrder;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}