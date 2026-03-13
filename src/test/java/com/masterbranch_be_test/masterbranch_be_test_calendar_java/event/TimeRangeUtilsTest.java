package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class TimeRangeUtilsTest {

    @Test
    void shouldDetectPartialAndFullOverlaps() {
        Instant queryStart = Instant.parse("2026-03-13T09:00:00Z");
        Instant queryEnd = Instant.parse("2026-03-13T11:00:00Z");

        assertTrue(TimeRangeUtils.isOverlap(
            Instant.parse("2026-03-13T10:30:00Z"),
            Instant.parse("2026-03-13T11:30:00Z"),
            queryStart,
            queryEnd
        ));

        assertTrue(TimeRangeUtils.isOverlap(
            Instant.parse("2026-03-13T08:00:00Z"),
            Instant.parse("2026-03-13T12:00:00Z"),
            queryStart,
            queryEnd
        ));

        assertFalse(TimeRangeUtils.isOverlap(
            Instant.parse("2026-03-13T11:00:00Z"),
            Instant.parse("2026-03-13T12:00:00Z"),
            queryStart,
            queryEnd
        ));
    }
}
