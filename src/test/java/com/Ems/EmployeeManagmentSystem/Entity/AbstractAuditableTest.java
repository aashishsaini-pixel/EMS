package com.Ems.EmployeeManagmentSystem.Entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AbstractAuditableTest {

    @Test
    void testAuditFields() {
        class TestAuditable extends AbstractAudiatable {}

        TestAuditable auditable = new TestAuditable();

        LocalDateTime now = LocalDateTime.now();
        auditable.setCreatedAt(now);
        auditable.setUpdatedAt(now.plusMinutes(1));

        assertEquals(now, auditable.getCreatedAt());
        assertEquals(now.plusMinutes(1), auditable.getUpdatedAt());
    }
}
