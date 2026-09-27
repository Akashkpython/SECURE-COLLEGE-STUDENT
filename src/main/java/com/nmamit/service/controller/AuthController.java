package com.nmamit.service.controller;

import com.nmamit.service.dto.*;
import com.nmamit.service.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public authentication endpoints — registration and login.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Student registration and login")
public class AuthController {

    private final StudentService studentService;

    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new student account")
    public ResponseEntity<StudentResponse> register(@Valid @RequestBody RegisterRequest request) {
        StudentResponse response = studentService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT token")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = studentService.login(request);
        return ResponseEntity.ok(response);
    }
}
