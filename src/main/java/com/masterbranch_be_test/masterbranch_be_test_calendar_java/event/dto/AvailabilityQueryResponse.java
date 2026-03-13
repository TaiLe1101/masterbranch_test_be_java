package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto;

import java.util.List;

public class AvailabilityQueryResponse {

    private Integer ownerId;
    private String timezone;
    private List<AvailabilitySlotResponse> slots;

    public AvailabilityQueryResponse(Integer ownerId, String timezone, List<AvailabilitySlotResponse> slots) {
        this.ownerId = ownerId;
        this.timezone = timezone;
        this.slots = slots;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<AvailabilitySlotResponse> getSlots() {
        return slots;
    }

    public void setSlots(List<AvailabilitySlotResponse> slots) {
        this.slots = slots;
    }
}
