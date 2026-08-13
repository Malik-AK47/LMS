package com.malik.lms.certificate.entity;

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
@Table(name = "certificates", uniqueConstraints = { @UniqueConstraint(name = "uk_certificate_course", columnNames = "course_id")})
public class Certificate {

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}