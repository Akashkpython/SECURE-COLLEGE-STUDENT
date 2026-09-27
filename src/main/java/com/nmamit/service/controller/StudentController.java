package com.nmamit.service.controller;

import com.nmamit.service.dto.StudentResponse;
import com.nmamit.service.dto.UpdateProfileRequest;
import com.nmamit.service.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Student profile endpoints.
 * Uses the authenticated identity from JWT — never a client-supplied student ID.
 */
@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Profile", description = "View and update own profile")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/me")
    @Operation(summary = "View own profile")
    public ResponseEntity<StudentResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(studentService.getProfile(email));
    }

    @PutMapping("/me")
    @Operation(summary = "Update own profile")
    public ResponseEntity<StudentResponse> updateProfile(Authentication authentication,
                                                          @Valid @RequestBody UpdateProfileRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(studentService.updateProfile(email, request));
    }
}
