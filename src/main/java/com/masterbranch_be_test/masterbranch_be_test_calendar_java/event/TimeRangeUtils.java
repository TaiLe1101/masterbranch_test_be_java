package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event;

import java.time.Instant;

public final class TimeRangeUtils {

    private TimeRangeUtils() {
    }

    public static boolean isOverlap(Instant aStart, Instant aEnd, Instant bStart, Instant bEnd) {
        return aStart.isBefore(bEnd) && aEnd.isAfter(bStart);
    }
}
