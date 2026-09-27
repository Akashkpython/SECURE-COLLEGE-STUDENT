package com.nmamit.service.repository;

import com.nmamit.service.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Student entity.
 * Uses Spring Data JPA — all queries are parameterized (safe from SQL injection).
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);
}
