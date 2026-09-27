package com.nmamit.service.dto;

import com.nmamit.service.entity.RequestCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new service request.
 */
public class CreateServiceRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private RequestCategory category;

    // ── Getters and Setters ─────────────────────────────────

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestCategory getCategory() {
        return category;
    }

    public void setCategory(RequestCategory category) {
        this.category = category;
    }
}
