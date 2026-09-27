package com.nmamit.service.service;

import com.nmamit.service.dto.CreateServiceRequestDTO;
import com.nmamit.service.dto.ServiceRequestResponse;
import com.nmamit.service.dto.UpdateStatusRequest;
import com.nmamit.service.entity.ServiceRequest;
import com.nmamit.service.entity.Student;
import com.nmamit.service.exception.ResourceNotFoundException;
import com.nmamit.service.repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles service request creation, retrieval and status updates.
 */
@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final StudentService studentService;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                  StudentService studentService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.studentService = studentService;
    }

    // ── Student operations ──────────────────────────────────

    /**
     * Create a new service request for the authenticated student.
     * Student identity comes from JWT — not from the request body.
     */
    @Transactional
    public ServiceRequestResponse createRequest(String studentEmail,
                                                 CreateServiceRequestDTO dto) {
        Student student = studentService.findByEmail(studentEmail);

        ServiceRequest request = new ServiceRequest();
        request.setStudent(student);
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setCategory(dto.getCategory());
        // Status defaults to OPEN (entity default)

        ServiceRequest saved = serviceRequestRepository.save(request);
        return ServiceRequestResponse.from(saved);
    }

    /**
     * Get all service requests belonging to the authenticated student.
     * Ownership enforced: only returns requests owned by the JWT identity.
     */
    public List<ServiceRequestResponse> getMyRequests(String studentEmail) {
        Student student = studentService.findByEmail(studentEmail);
        return serviceRequestRepository
                .findByStudentIdOrderByCreatedAtDesc(student.getId())
                .stream()
                .map(ServiceRequestResponse::from)
                .collect(Collectors.toList());
    }

    // ── Admin operations ────────────────────────────────────

    /**
     * Get all service requests (admin view).
     */
    public List<ServiceRequestResponse> getAllRequests() {
        return serviceRequestRepository.findAll()
                .stream()
                .map(ServiceRequestResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Update the status of a service request (admin only).
     */
    @Transactional
    public ServiceRequestResponse updateStatus(Long requestId, UpdateStatusRequest dto) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service request not found with id: " + requestId));

        request.setStatus(dto.getStatus());
        ServiceRequest saved = serviceRequestRepository.save(request);
        return ServiceRequestResponse.from(saved);
    }
}
