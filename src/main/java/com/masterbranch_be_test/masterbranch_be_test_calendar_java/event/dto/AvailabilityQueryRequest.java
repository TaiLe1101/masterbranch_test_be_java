package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto;

import java.time.Instant;
import java.time.LocalTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AvailabilityQueryRequest {

    @NotNull(message = "ownerId is required")
    private Integer ownerId;

    @NotNull(message = "from is required")
    private Instant from;

    @NotNull(message = "to is required")
    private Instant to;

    @NotNull(message = "timezone is required")
    private String timezone;

    @NotNull(message = "slotDuration is required")
    @Min(value = 1, message = "slotDuration must be >= 1")
    @Max(value = 480, message = "slotDuration must be <= 480")
    private Integer slotDuration;

    @Valid
    @NotNull(message = "workingHours is required")
    private WorkingHours workingHours;

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getSlotDuration() {
        return slotDuration;
    }

    public void setSlotDuration(Integer slotDuration) {
        this.slotDuration = slotDuration;
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(WorkingHours workingHours) {
        this.workingHours = workingHours;
    }

    public static class WorkingHours {

        @NotNull(message = "workingHours.start is required")
        private LocalTime start;

        @NotNull(message = "workingHours.end is required")
        private LocalTime end;

        public LocalTime getStart() {
            return start;
        }

        public void setStart(LocalTime start) {
            this.start = start;
        }

        public LocalTime getEnd() {
            return end;
        }

        public void setEnd(LocalTime end) {
            this.end = end;
        }
    }
}
