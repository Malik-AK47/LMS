package com.malik.lms.certificate.repository;

import com.malik.lms.certificate.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByCourseId(Long courseId);

    boolean existsByCourseId(Long courseId);
}