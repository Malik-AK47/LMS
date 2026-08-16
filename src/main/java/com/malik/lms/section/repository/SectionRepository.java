package com.malik.lms.section.repository;

import com.malik.lms.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    boolean existsByCourseIdAndDisplayOrder(Long courseId, Integer displayOrder);

    Optional<Section> findByIdAndCourseInstructorId(Long sectionId, Long instructorId);

    @Modifying
    @Query("""
        UPDATE Section s
        SET s.displayOrder = s.displayOrder + 1
        WHERE s.course.id = :courseId
        AND s.displayOrder >= :newOrder
        AND s.displayOrder < :oldOrder
    """)
    void shiftSectionsDown(
            @Param("courseId") Long courseId,
            @Param("newOrder") Integer newOrder,
            @Param("oldOrder") Integer oldOrder
    );

    @Modifying
    @Query("""
        UPDATE Section s
        SET s.displayOrder = s.displayOrder - 1
        WHERE s.course.id = :courseId
        AND s.displayOrder > :oldOrder
        AND s.displayOrder <= :newOrder
    """)
    void shiftSectionsUp(
            @Param("courseId") Long courseId,
            @Param("oldOrder") Integer oldOrder,
            @Param("newOrder") Integer newOrder
    );

    List<Section> findByCourseIdOrderByDisplayOrderAsc(Long courseId);

    @Modifying
    @Query("""
        DELETE FROM Section s
        WHERE s.course.id = :courseId
        """)
    void deleteByCourseId(@Param("courseId") Long courseId);
}
