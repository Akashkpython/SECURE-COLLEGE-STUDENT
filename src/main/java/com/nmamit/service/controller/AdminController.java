package com.nmamit.service.controller;

import com.nmamit.service.dto.ServiceRequestResponse;
import com.nmamit.service.dto.StudentResponse;
import com.nmamit.service.dto.UpdateStatusRequest;
import com.nmamit.service.service.AdminService;
import com.nmamit.service.service.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only endpoints.
 * Access restricted to ADMIN role via SecurityConfig.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin operations – view students, manage requests")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;
    private final ServiceRequestService serviceRequestService;

    public AdminController(AdminService adminService,
                           ServiceRequestService serviceRequestService) {
        this.adminService = adminService;
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/students")
    @Operation(summary = "View all students")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @GetMapping("/requests")
    @Operation(summary = "View all service requests")
    public ResponseEntity<List<ServiceRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(serviceRequestService.getAllRequests());
    }

    @PutMapping("/requests/{id}/status")
    @Operation(summary = "Update the status of a service request")
    public ResponseEntity<ServiceRequestResponse> updateRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(serviceRequestService.updateStatus(id, request));
    }
}
