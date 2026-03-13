package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto;

public class AvailabilitySlotResponse {

    private String start;
    private String end;

    public AvailabilitySlotResponse(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}