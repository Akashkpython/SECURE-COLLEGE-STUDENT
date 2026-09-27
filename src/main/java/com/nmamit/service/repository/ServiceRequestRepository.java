package com.nmamit.service.repository;

import com.nmamit.service.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ServiceRequest entity.
 * Uses Spring Data JPA — all queries are parameterized (safe from SQL injection).
 */
@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
