package com.malik.lms.certificate.repository;

import com.malik.lms.certificate.entity.IssuedCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssuedCertificateRepository extends JpaRepository<IssuedCertificate, Long> {
    Optional<IssuedCertificate> findByEnrollmentId(Long enrollmentId);

    boolean existsByEnrollmentId(Long enrollmentId);

    Optional<IssuedCertificate> findByCertificateNumber(String certificateNumber);

    Optional<IssuedCertificate> findByEnrollmentUserIdAndEnrollmentCourseId(Long userId, Long courseId);

    @Modifying
    @Query("""
        DELETE FROM IssuedCertificate ic
        WHERE ic.certificate.course.id = :courseId
        """)
    void deleteByCourseId(@Param("courseId") Long courseId);
}