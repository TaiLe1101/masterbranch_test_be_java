package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto;

import java.time.Instant;

public class TimeSlotResponse {

    private Instant startAt;
    private Instant endAt;

    public TimeSlotResponse() {
    }

    public TimeSlotResponse(Instant startAt, Instant endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }
}
