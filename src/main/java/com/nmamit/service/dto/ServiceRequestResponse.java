package com.nmamit.service.dto;

import com.nmamit.service.entity.RequestCategory;
import com.nmamit.service.entity.RequestStatus;
import com.nmamit.service.entity.ServiceRequest;

import java.time.LocalDateTime;

/**
 * DTO for returning service request data.
 */
public class ServiceRequestResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String title;
    private String description;
    private RequestCategory category;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ServiceRequestResponse from(ServiceRequest request) {
        ServiceRequestResponse dto = new ServiceRequestResponse();
        dto.id = request.getId();
        dto.studentId = request.getStudent().getId();
        dto.studentName = request.getStudent().getName();
        dto.title = request.getTitle();
        dto.description = request.getDescription();
        dto.category = request.getCategory();
        dto.status = request.getStatus();
        dto.createdAt = request.getCreatedAt();
        dto.updatedAt = request.getUpdatedAt();
        return dto;
    }

    // ── Getters ─────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public RequestCategory getCategory() {
        return category;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
