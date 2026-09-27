package com.nmamit.service.config;

import com.nmamit.service.entity.Role;
import com.nmamit.service.entity.Student;
import com.nmamit.service.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds an ADMIN account on startup so the admin role can be tested
 * without exposing an admin-registration endpoint.
 *
 * Uses ApplicationReadyEvent to ensure the database schema is fully
 * initialized before attempting to query or insert data.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAdmin() {
        String adminEmail = "admin@nmamit.ac.in";

        if (studentRepository.existsByEmail(adminEmail)) {
            log.info("Admin account already exists — skipping seed.");
            return;
        }

        Student admin = new Student();
        admin.setName("System Admin");
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode("Admin@12345"));
        admin.setPhone("0000000000");
        admin.setCourse("ADMIN");
        admin.setYear(0);
        admin.setRole(Role.ADMIN);

        studentRepository.save(admin);
        log.info("Seeded default admin account: {}", adminEmail);
    }
}
