package org.example.library.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AudioBookTest {

    @Test
    void getSummary_shouldContainNarratorAndDuration() {
        AudioBook ab = new AudioBook(
                "AB1",
                "Atomic Habits",
                2018,
                "James Clear",
                "9780735211292",
                "John Doe",
                300
        );

        String summary = ab.getSummary();
        assertNotNull(summary);
        assertTrue(summary.toLowerCase().contains("atomic habits"));
        assertTrue(summary.toLowerCase().contains("james"));
        assertTrue(summary.toLowerCase().contains("john doe"));
        assertTrue(summary.contains("300"));
    }
}