package com.rsl.leavemanagement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test verifying that the Spring Boot ApplicationContext loads
 * cleanly with all beans, auto-configuration, and profiles active.
 */
@SpringBootTest
class LeaveManagementApplicationTests {

    @Test
    @DisplayName("Application Context loads successfully without configuration errors")
    void contextLoads() {
        // Assertion is implicit: fails if ApplicationContext cannot start.
    }
}
