package org.example.library.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagazineTest {

    @Test
    void getSummary_shouldContainImportantFields() {
        Magazine mag = new Magazine("MG1", "Tech Monthly", 2024, 12, "TechPub");
        String summary = mag.getSummary();

        assertNotNull(summary);
        assertTrue(summary.toLowerCase().contains("tech monthly"));
        assertTrue(summary.toLowerCase().contains("issue"));
        assertTrue(summary.toLowerCase().contains("techpub"));
    }
}