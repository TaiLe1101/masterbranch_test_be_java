package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.CreateEventRequest;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.EventResponse;

public final class EventMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private EventMapper() {
    }

    public static Event toEntity(CreateEventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setStartAt(request.getStartAt());
        event.setEndAt(request.getEndAt());
        event.setTimezone(request.getTimezone());
        event.setType(request.getType());
        event.setOwnerId(request.getOwnerId());
        event.setNotes(request.getNotes());
        event.setLocation(request.getLocation());
        event.setAttendees(request.getAttendees() == null ? new ArrayList<>() : new ArrayList<>(request.getAttendees()));
        return event;
    }

    public static EventResponse toResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        ZoneId zoneId = ZoneId.of(event.getTimezone());
        response.setStartAt(event.getStartAt().atZone(zoneId).format(FORMATTER));
        response.setEndAt(event.getEndAt().atZone(zoneId).format(FORMATTER));
        response.setTimezone(event.getTimezone());
        response.setType(event.getType());
        response.setOwnerId(event.getOwnerId());
        response.setNotes(event.getNotes());
        response.setLocation(event.getLocation());
        response.setAttendees(event.getAttendees());
        return response;
    }
}
