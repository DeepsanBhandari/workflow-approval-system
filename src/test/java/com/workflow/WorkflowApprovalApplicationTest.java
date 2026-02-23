package com.workflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class WorkflowApprovalApplicationTest {

    @Test
    void contextLoads() {
        // Verifies that the Spring context loads without errors
    }
}
