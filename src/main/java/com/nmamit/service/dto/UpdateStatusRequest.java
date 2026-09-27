package com.nmamit.service.dto;

import com.nmamit.service.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for admin status update of a service request.
 */
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private RequestStatus status;

    // ── Getters and Setters ─────────────────────────────────

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
