package com.malik.lms.section.entity;

import com.malik.lms.course.entity.Course;
import com.malik.lms.lesson.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sections", uniqueConstraints = { @UniqueConstraint(name = "uk_section_course_order", columnNames = {"course_id", "display_order"})})
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer displayOrder;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "section")
    private List<Lesson> lessons = new ArrayList<>();
}