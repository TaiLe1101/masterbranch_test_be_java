package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.AvailabilityQueryRequest;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.TimeSlotResponse;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.exception.ApiException;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    @Test
    void shouldReturnAllWorkingHoursAsSlotsWhenNoEvents() {
        AvailabilityQueryRequest request = availabilityRequest(
            Instant.parse("2026-03-12T00:00:00Z"),
            Instant.parse("2026-03-12T23:59:59Z"),
            "UTC",
            60,
            LocalTime.of(9, 0),
            LocalTime.of(17, 0)
        );

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(eq(1), any(Instant.class), any(Instant.class)))
            .thenReturn(List.of());

        List<TimeSlotResponse> result = availabilityService.queryAvailability(request);

        assertEquals(8, result.size());
        assertEquals(Instant.parse("2026-03-12T09:00:00Z"), result.get(0).getStartAt());
        assertEquals(Instant.parse("2026-03-12T17:00:00Z"), result.get(7).getEndAt());
    }

    @Test
    void shouldExcludeTimeOccupiedByEvents() {
        AvailabilityQueryRequest request = availabilityRequest(
            Instant.parse("2026-03-12T00:00:00Z"),
            Instant.parse("2026-03-12T23:59:59Z"),
            "UTC",
            60,
            LocalTime.of(9, 0),
            LocalTime.of(17, 0)
        );

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(eq(1), any(Instant.class), any(Instant.class)))
            .thenReturn(List.of(event(Instant.parse("2026-03-12T10:00:00Z"), Instant.parse("2026-03-12T11:00:00Z"))));

        List<TimeSlotResponse> result = availabilityService.queryAvailability(request);

        assertEquals(7, result.size());
        List<Instant> starts = result.stream().map(TimeSlotResponse::getStartAt).toList();
        assertFalse(starts.contains(Instant.parse("2026-03-12T10:00:00Z")));
    }

    @Test
    void shouldHandleMultipleEventsInADay() {
        AvailabilityQueryRequest request = availabilityRequest(
            Instant.parse("2026-03-12T00:00:00Z"),
            Instant.parse("2026-03-12T23:59:59Z"),
            "UTC",
            60,
            LocalTime.of(9, 0),
            LocalTime.of(17, 0)
        );

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(eq(1), any(Instant.class), any(Instant.class)))
            .thenReturn(List.of(
                event(Instant.parse("2026-03-12T09:00:00Z"), Instant.parse("2026-03-12T10:00:00Z")),
                event(Instant.parse("2026-03-12T12:00:00Z"), Instant.parse("2026-03-12T13:00:00Z"))
            ));

        List<TimeSlotResponse> result = availabilityService.queryAvailability(request);

        assertEquals(6, result.size());
    }

    @Test
    void shouldReturnSlotsWithCorrectDurationForThirtyMinutes() {
        AvailabilityQueryRequest request = availabilityRequest(
            Instant.parse("2026-03-12T00:00:00Z"),
            Instant.parse("2026-03-12T23:59:59Z"),
            "UTC",
            30,
            LocalTime.of(9, 0),
            LocalTime.of(11, 0)
        );

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(eq(1), any(Instant.class), any(Instant.class)))
            .thenReturn(List.of());

        List<TimeSlotResponse> result = availabilityService.queryAvailability(request);

        assertEquals(4, result.size());
        assertEquals(Instant.parse("2026-03-12T09:00:00Z"), result.get(0).getStartAt());
        assertEquals(Instant.parse("2026-03-12T09:30:00Z"), result.get(0).getEndAt());
        assertEquals(Instant.parse("2026-03-12T09:30:00Z"), result.get(1).getStartAt());
        assertEquals(Instant.parse("2026-03-12T10:00:00Z"), result.get(1).getEndAt());
    }

    @Test
    void shouldNotReturnSlotsShorterThanRequestedDuration() {
        AvailabilityQueryRequest request = availabilityRequest(
            Instant.parse("2026-03-12T00:00:00Z"),
            Instant.parse("2026-03-12T23:59:59Z"),
            "UTC",
            60,
            LocalTime.of(9, 0),
            LocalTime.of(11, 0)
        );

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(eq(1), any(Instant.class), any(Instant.class)))
            .thenReturn(List.of(event(Instant.parse("2026-03-12T09:45:00Z"), Instant.parse("2026-03-12T10:00:00Z"))));

        List<TimeSlotResponse> result = availabilityService.queryAvailability(request);

        assertEquals(1, result.size());
        assertEquals(Instant.parse("2026-03-12T10:00:00Z"), result.get(0).getStartAt());
    }

    @Test
    void shouldHandleEventsSpanningMultipleDaysCorrectly() {
        AvailabilityQueryRequest request = availabilityRequest(
            Instant.parse("2026-03-12T00:00:00Z"),
            Instant.parse("2026-03-13T23:59:59Z"),
            "UTC",
            60,
            LocalTime.of(9, 0),
            LocalTime.of(17, 0)
        );

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(eq(1), any(Instant.class), any(Instant.class)))
            .thenReturn(List.of());

        List<TimeSlotResponse> result = availabilityService.queryAvailability(request);

        assertEquals(16, result.size());
    }

    @Test
    void shouldRejectInvalidWorkingRange() {
        AvailabilityQueryRequest request = availabilityRequest(
            Instant.parse("2026-03-13T09:00:00Z"),
            Instant.parse("2026-03-13T18:00:00Z"),
            "UTC",
            30,
            LocalTime.of(13, 0),
            LocalTime.of(12, 0)
        );

        ApiException ex = assertThrows(ApiException.class, () -> availabilityService.queryAvailability(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void shouldRejectInvalidQueryRange() {
        AvailabilityQueryRequest request = availabilityRequest(
            Instant.parse("2026-03-13T12:00:00Z"),
            Instant.parse("2026-03-13T09:00:00Z"),
            "UTC",
            30,
            LocalTime.of(9, 0),
            LocalTime.of(17, 0)
        );

        ApiException ex = assertThrows(ApiException.class, () -> availabilityService.queryAvailability(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    private AvailabilityQueryRequest availabilityRequest(
        Instant from,
        Instant to,
        String timezone,
        Integer slotDuration,
        LocalTime workStart,
        LocalTime workEnd
    ) {
        AvailabilityQueryRequest request = new AvailabilityQueryRequest();
        request.setOwnerId(1);
        request.setFrom(from);
        request.setTo(to);
        request.setTimezone(timezone);
        request.setSlotDuration(slotDuration);

        AvailabilityQueryRequest.WorkingHours workingHours = new AvailabilityQueryRequest.WorkingHours();
        workingHours.setStart(workStart);
        workingHours.setEnd(workEnd);
        request.setWorkingHours(workingHours);
        return request;
    }

    private Event event(Instant startAt, Instant endAt) {
        Event event = new Event();
        event.setStartAt(startAt);
        event.setEndAt(endAt);
        event.setOwnerId(1);
        event.setTimezone("UTC");
        return event;
    }
}
