package com.nmamit.service.dto;

import com.nmamit.service.entity.Role;
import com.nmamit.service.entity.Student;

import java.time.LocalDateTime;

/**
 * DTO for returning student profile data.
 * Never exposes the password hash.
 */
public class StudentResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String course;
    private Integer year;
    private Role role;
    private LocalDateTime createdAt;

    public static StudentResponse from(Student student) {
        StudentResponse dto = new StudentResponse();
        dto.id = student.getId();
        dto.name = student.getName();
        dto.email = student.getEmail();
        dto.phone = student.getPhone();
        dto.course = student.getCourse();
        dto.year = student.getYear();
        dto.role = student.getRole();
        dto.createdAt = student.getCreatedAt();
        return dto;
    }

    // ── Getters ─────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCourse() {
        return course;
    }

    public Integer getYear() {
        return year;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
