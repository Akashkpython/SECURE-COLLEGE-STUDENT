package com.nmamit.service.service;

import com.nmamit.service.dto.*;
import com.nmamit.service.entity.Role;
import com.nmamit.service.entity.Student;
import com.nmamit.service.exception.BadRequestException;
import com.nmamit.service.exception.InvalidCredentialsException;
import com.nmamit.service.exception.ResourceNotFoundException;
import com.nmamit.service.repository.StudentRepository;
import com.nmamit.service.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles authentication (register, login) and student profile operations.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public StudentService(StudentRepository studentRepository,
                          PasswordEncoder passwordEncoder,
                          JwtProvider jwtProvider) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    // ── Authentication ──────────────────────────────────────

    /**
     * Register a new STUDENT account.
     * Role is always STUDENT — prevents privilege escalation.
     */
    @Transactional
    public StudentResponse register(RegisterRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        student.setPhone(request.getPhone());
        student.setCourse(request.getCourse());
        student.setYear(request.getYear());
        student.setRole(Role.STUDENT); // Always STUDENT — never trust client input for role

        Student saved = studentRepository.save(student);
        return StudentResponse.from(saved);
    }

    /**
     * Authenticate and return a JWT.
     */
    public JwtResponse login(LoginRequest request) {
        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), student.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtProvider.generateToken(student.getEmail(), student.getRole().name());
        return new JwtResponse(token, student.getEmail(), student.getRole().name());
    }

    // ── Profile ─────────────────────────────────────────────

    /**
     * Get the profile of the currently authenticated student.
     * Uses the email from the JWT — not a client-supplied ID.
     */
    public StudentResponse getProfile(String email) {
        Student student = findByEmail(email);
        return StudentResponse.from(student);
    }

    /**
     * Update the profile of the currently authenticated student.
     * Only allows updating name, phone, course, and year.
     */
    @Transactional
    public StudentResponse updateProfile(String email, UpdateProfileRequest request) {
        Student student = findByEmail(email);

        if (request.getName() != null) {
            student.setName(request.getName());
        }
        if (request.getPhone() != null) {
            student.setPhone(request.getPhone());
        }
        if (request.getCourse() != null) {
            student.setCourse(request.getCourse());
        }
        if (request.getYear() != null) {
            student.setYear(request.getYear());
        }

        Student saved = studentRepository.save(student);
        return StudentResponse.from(saved);
    }

    // ── Internal helpers ────────────────────────────────────

    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }
}
