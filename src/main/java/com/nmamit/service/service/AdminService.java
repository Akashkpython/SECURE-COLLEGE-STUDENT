package com.nmamit.service.service;

import com.nmamit.service.dto.StudentResponse;
import com.nmamit.service.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin-specific operations.
 */
@Service
public class AdminService {

    private final StudentRepository studentRepository;

    public AdminService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Get all registered students (admin view).
     */
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentResponse::from)
                .collect(Collectors.toList());
    }
}
