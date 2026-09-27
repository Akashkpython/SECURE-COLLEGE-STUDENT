package com.nmamit.service.controller;

import com.nmamit.service.dto.CreateServiceRequestDTO;
import com.nmamit.service.dto.ServiceRequestResponse;
import com.nmamit.service.service.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Student service request endpoints.
 * Ownership enforced via JWT identity.
 */
@RestController
@RequestMapping("/api/requests")
@Tag(name = "Service Requests", description = "Create and view service requests")
@SecurityRequirement(name = "bearerAuth")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping
    @Operation(summary = "Create a new service request")
    public ResponseEntity<ServiceRequestResponse> createRequest(
            Authentication authentication,
            @Valid @RequestBody CreateServiceRequestDTO dto) {
        String email = authentication.getName();
        ServiceRequestResponse response = serviceRequestService.createRequest(email, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "View own service requests")
    public ResponseEntity<List<ServiceRequestResponse>> getMyRequests(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(serviceRequestService.getMyRequests(email));
    }
}
