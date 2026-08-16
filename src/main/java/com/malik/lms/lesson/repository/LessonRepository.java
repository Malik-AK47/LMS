package com.malik.lms.lesson.repository;

import com.malik.lms.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    boolean existsBySectionIdAndDisplayOrder(Long sectionId, Integer displayOrder);

    Optional<Lesson> findByIdAndSectionCourseInstructorId(Long lessonId, Long instructorId);

    @Modifying
    @Query("""
    UPDATE Lesson l
    SET l.displayOrder = l.displayOrder - 1
    WHERE l.section.id = :sectionId
      AND l.displayOrder > :oldOrder
      AND l.displayOrder <= :newOrder
""")
    void shiftLessonsUp(@Param("sectionId") Long sectionId, @Param("oldOrder") Integer oldOrder, @Param("newOrder") Integer newOrder);

    @Modifying
    @Query("""
    UPDATE Lesson l
    SET l.displayOrder = l.displayOrder + 1
    WHERE l.section.id = :sectionId
      AND l.displayOrder >= :newOrder
      AND l.displayOrder < :oldOrder
""")
    void shiftLessonsDown(@Param("sectionId") Long sectionId, @Param("newOrder") Integer newOrder, @Param("oldOrder") Integer oldOrder);

    @Modifying
    @Query("""
    UPDATE Lesson l
    SET l.displayOrder = l.displayOrder - 1
    WHERE l.section.id = :sectionId
      AND l.displayOrder > :displayOrder
""")
    void shiftLessonsAfterDelete(@Param("sectionId") Long sectionId, @Param("displayOrder") Integer displayOrder);

    long countBySectionCourseId(Long courseId);

    void deleteBySectionId(Long sectionId);

    @Modifying
    @Query("""
        DELETE FROM Lesson l
        WHERE l.section.course.id = :courseId
        """)
    void deleteByCourseId(@Param("courseId") Long courseId);
}
