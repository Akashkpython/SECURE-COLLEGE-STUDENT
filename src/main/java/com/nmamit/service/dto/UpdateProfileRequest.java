package com.nmamit.service.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating student profile.
 * Only name, phone, course and year are editable.
 * Email and role are NOT updatable by the student.
 */
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Pattern(regexp = "^\\d{10}$", message = "Phone must be a 10-digit number")
    private String phone;

    @Size(max = 50, message = "Course must not exceed 50 characters")
    private String course;

    private Integer year;

    // ── Getters and Setters ─────────────────────────────────

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
