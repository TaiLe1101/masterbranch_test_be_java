package com.masterbranch_be_test.masterbranch_be_test_calendar_java.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.common.ResponseApi;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.EventService;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.CreateEventRequest;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.EventResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<ResponseApi<EventResponse>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        EventResponse result = eventService.createEvent(request);
        ResponseApi<EventResponse> response = new ResponseApi<>(HttpStatus.CREATED.value(), "Event created successfully", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ResponseApi<List<EventResponse>>> getEvents(
        @RequestParam @NotNull Integer ownerId,
        @RequestParam @NotNull Instant from,
        @RequestParam @NotNull Instant to
    ) {
        List<EventResponse> result = eventService.getEvents(ownerId, from, to);
        ResponseApi<List<EventResponse>> response = new ResponseApi<>(HttpStatus.OK.value(), "Events fetched successfully", result);
        return ResponseEntity.ok(response);
    }
}
