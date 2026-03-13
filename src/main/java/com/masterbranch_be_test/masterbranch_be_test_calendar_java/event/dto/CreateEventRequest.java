package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto;

import java.time.Instant;
import java.util.List;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.EventType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateEventRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotNull(message = "startAt is required")
    private Instant startAt;

    @NotNull(message = "endAt is required")
    private Instant endAt;

    @NotBlank(message = "timezone is required")
    private String timezone;

    @NotNull(message = "type is required")
    private EventType type;

    @NotNull(message = "ownerId is required")
    private Integer ownerId;

    private String notes;
    private String location;
    private List<String> attendees;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }
}
