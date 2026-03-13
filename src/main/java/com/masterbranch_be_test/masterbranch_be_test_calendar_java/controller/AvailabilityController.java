package com.masterbranch_be_test.masterbranch_be_test_calendar_java.controller;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.common.ResponseApi;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.AvailabilityService;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.AvailabilityQueryRequest;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.AvailabilityQueryResponse;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.AvailabilitySlotResponse;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.TimeSlotResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/availability")
public class AvailabilityController {

    private static final DateTimeFormatter SLOT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping("/query")
    public ResponseEntity<ResponseApi<AvailabilityQueryResponse>> query(@Valid @RequestBody AvailabilityQueryRequest request) {
        List<TimeSlotResponse> slots = availabilityService.queryAvailability(request);
        ZoneId zoneId = ZoneId.of(request.getTimezone());

        List<AvailabilitySlotResponse> slotResponses = slots.stream()
            .map(slot -> new AvailabilitySlotResponse(
                slot.getStartAt().atZone(zoneId).format(SLOT_FORMATTER),
                slot.getEndAt().atZone(zoneId).format(SLOT_FORMATTER)
            ))
            .toList();

        AvailabilityQueryResponse data = new AvailabilityQueryResponse(request.getOwnerId(), request.getTimezone(), slotResponses);
        ResponseApi<AvailabilityQueryResponse> response = new ResponseApi<>(HttpStatus.OK.value(), "Available slots retrieved", data);
        return ResponseEntity.ok(response);
    }
}
