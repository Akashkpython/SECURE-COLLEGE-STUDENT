package com.nmamit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nmamit.service.dto.*;
import com.nmamit.service.entity.RequestCategory;
import com.nmamit.service.entity.RequestStatus;
import com.nmamit.service.entity.Role;
import com.nmamit.service.entity.Student;
import com.nmamit.service.repository.StudentRepository;
import com.nmamit.service.security.JwtProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests covering all API endpoints and security requirements.
 * Uses MockMvc for full Spring context testing.
 *
 * Test matrix covers:
 * - Valid/invalid registration and login
 * - JWT authentication enforcement
 * - Role-based access control (STUDENT vs ADMIN)
 * - Ownership checks (BOLA/IDOR prevention)
 * - Input validation
 * - SQL injection resistance
 * - Safe error responses
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SecureServiceRequestApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String studentToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Generate tokens for testing
        studentToken = jwtProvider.generateToken("rahul@example.com", "STUDENT");
        adminToken = jwtProvider.generateToken("admin@nmamit.ac.in", "ADMIN");
    }

    // ════════════════════════════════════════════════════════
    // REGISTRATION TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("Valid registration returns 201 and student data")
    void testValidRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Rahul Kumar");
        request.setEmail("rahul@example.com");
        request.setPassword("Rahul@12345");
        request.setPhone("9876543210");
        request.setCourse("MCA");
        request.setYear(1);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rahul Kumar"))
                .andExpect(jsonPath("$.email").value("rahul@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                // Verify password hash is NOT exposed
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @Order(2)
    @DisplayName("Duplicate email registration returns 400")
    void testDuplicateEmailRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Rahul Clone");
        request.setEmail("rahul@example.com");
        request.setPassword("Clone@12345");
        request.setPhone("9876543211");
        request.setCourse("MCA");
        request.setYear(1);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }

    @Test
    @Order(3)
    @DisplayName("Invalid email format returns 400")
    void testInvalidEmailRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Bad Email");
        request.setEmail("not-an-email");
        request.setPassword("Valid@12345");
        request.setPhone("9876543212");
        request.setCourse("MCA");
        request.setYear(1);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem(containsString("email"))));
    }

    @Test
    @Order(4)
    @DisplayName("Weak password returns 400")
    void testWeakPasswordRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Weak Pass");
        request.setEmail("weak@example.com");
        request.setPassword("12345");
        request.setPhone("9876543213");
        request.setCourse("MCA");
        request.setYear(1);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ════════════════════════════════════════════════════════
    // LOGIN TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(10)
    @DisplayName("Valid login returns 200 and JWT token")
    void testValidLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("rahul@example.com");
        request.setPassword("Rahul@12345");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.email").value("rahul@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    @Order(11)
    @DisplayName("Wrong password returns 401 Unauthorized")
    void testWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("rahul@example.com");
        request.setPassword("WrongPassword@1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @Order(12)
    @DisplayName("Non-existent email login returns 401 Unauthorized")
    void testNonExistentEmailLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("nobody@example.com");
        request.setPassword("Nobody@12345");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // ════════════════════════════════════════════════════════
    // JWT AUTHENTICATION TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(20)
    @DisplayName("No JWT returns 401 Unauthorized")
    void testNoJwt() throws Exception {
        mockMvc.perform(get("/api/students/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(21)
    @DisplayName("Invalid JWT returns 401 Unauthorized")
    void testInvalidJwt() throws Exception {
        mockMvc.perform(get("/api/students/me")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(22)
    @DisplayName("Malformed Authorization header returns 401")
    void testMalformedAuthHeader() throws Exception {
        mockMvc.perform(get("/api/students/me")
                        .header("Authorization", "NotBearer " + studentToken))
                .andExpect(status().isUnauthorized());
    }

    // ════════════════════════════════════════════════════════
    // STUDENT PROFILE TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(30)
    @DisplayName("Student can view own profile")
    void testViewOwnProfile() throws Exception {
        mockMvc.perform(get("/api/students/me")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("rahul@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @Order(31)
    @DisplayName("Student can update own profile")
    void testUpdateOwnProfile() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Rahul K.");
        request.setPhone("9999999999");

        mockMvc.perform(put("/api/students/me")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rahul K."))
                .andExpect(jsonPath("$.phone").value("9999999999"));
    }

    // ════════════════════════════════════════════════════════
    // SERVICE REQUEST TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(40)
    @DisplayName("Student can create a service request")
    void testCreateServiceRequest() throws Exception {
        CreateServiceRequestDTO dto = new CreateServiceRequestDTO();
        dto.setTitle("Wi-Fi not working");
        dto.setDescription("Wi-Fi is not working in Lab 2.");
        dto.setCategory(RequestCategory.WIFI);

        mockMvc.perform(post("/api/requests")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Wi-Fi not working"))
                .andExpect(jsonPath("$.category").value("WIFI"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @Order(41)
    @DisplayName("Empty title returns 400")
    void testEmptyTitleServiceRequest() throws Exception {
        CreateServiceRequestDTO dto = new CreateServiceRequestDTO();
        dto.setTitle("");
        dto.setDescription("Some description");
        dto.setCategory(RequestCategory.WIFI);

        mockMvc.perform(post("/api/requests")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(42)
    @DisplayName("Student can view own service requests")
    void testViewOwnRequests() throws Exception {
        mockMvc.perform(get("/api/requests")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // ════════════════════════════════════════════════════════
    // ROLE-BASED ACCESS CONTROL TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(50)
    @DisplayName("Student calling admin endpoint returns 403 Forbidden")
    void testStudentCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/students")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(51)
    @DisplayName("Student cannot view all requests via admin endpoint")
    void testStudentCannotViewAllRequests() throws Exception {
        mockMvc.perform(get("/api/admin/requests")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(52)
    @DisplayName("Student cannot update request status via admin endpoint")
    void testStudentCannotUpdateStatus() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(RequestStatus.RESOLVED);

        mockMvc.perform(put("/api/admin/requests/1/status")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ════════════════════════════════════════════════════════
    // ADMIN ENDPOINT TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(60)
    @DisplayName("Admin can view all students")
    void testAdminViewStudents() throws Exception {
        mockMvc.perform(get("/api/admin/students")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(61)
    @DisplayName("Admin can view all service requests")
    void testAdminViewAllRequests() throws Exception {
        mockMvc.perform(get("/api/admin/requests")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(62)
    @DisplayName("Admin can update request status")
    void testAdminUpdateRequestStatus() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(RequestStatus.IN_PROGRESS);

        mockMvc.perform(put("/api/admin/requests/1/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    // ════════════════════════════════════════════════════════
    // SQL INJECTION TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(70)
    @DisplayName("SQL injection in login email is rejected")
    void testSqlInjectionInLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("' OR 1=1 --");
        request.setPassword("anything");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(71)
    @DisplayName("SQL injection in service request title is handled safely")
    void testSqlInjectionInRequestTitle() throws Exception {
        CreateServiceRequestDTO dto = new CreateServiceRequestDTO();
        dto.setTitle("'; DROP TABLE students; --");
        dto.setDescription("SQL injection attempt");
        dto.setCategory(RequestCategory.OTHER);

        mockMvc.perform(post("/api/requests")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Verify students table still exists and is accessible
        mockMvc.perform(get("/api/students/me")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    // ════════════════════════════════════════════════════════
    // MALFORMED REQUEST TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(80)
    @DisplayName("Malformed JSON returns safe error response")
    void testMalformedJson() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                // Should NOT contain stack trace or internal details
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    @Order(81)
    @DisplayName("Request to non-existent resource returns 404")
    void testAdminUpdateNonExistentRequest() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(RequestStatus.RESOLVED);

        mockMvc.perform(put("/api/admin/requests/99999/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ════════════════════════════════════════════════════════
    // BOLA / IDOR PREVENTION TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @Order(90)
    @DisplayName("Student only sees own requests — BOLA protection")
    void testStudentCanOnlySeeOwnRequests() throws Exception {
        // Register a second student
        RegisterRequest reg = new RegisterRequest();
        reg.setName("Priya Sharma");
        reg.setEmail("priya@example.com");
        reg.setPassword("Priya@12345");
        reg.setPhone("8765432109");
        reg.setCourse("MCA");
        reg.setYear(1);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        // Login as second student
        String priyaToken = jwtProvider.generateToken("priya@example.com", "STUDENT");

        // Create a request as Priya
        CreateServiceRequestDTO dto = new CreateServiceRequestDTO();
        dto.setTitle("Library AC not working");
        dto.setDescription("Library air conditioning is broken.");
        dto.setCategory(RequestCategory.LIBRARY);

        mockMvc.perform(post("/api/requests")
                        .header("Authorization", "Bearer " + priyaToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Rahul should NOT see Priya's request
        MvcResult result = mockMvc.perform(get("/api/requests")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        // Rahul's requests should only contain his email, not Priya's
        org.junit.jupiter.api.Assertions.assertFalse(body.contains("priya@example.com"));
    }
}
