package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.CreateEventRequest;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.EventResponse;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.exception.ApiException;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldThrowBadRequestWhenEndAtIsBeforeStartAt() {
        CreateEventRequest request = baseRequest();
        request.setStartAt(Instant.parse("2026-03-12T14:00:00Z"));
        request.setEndAt(Instant.parse("2026-03-12T10:00:00Z"));

        ApiException ex = assertThrows(ApiException.class, () -> eventService.createEvent(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("endAt must be greater than startAt", ex.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldThrowBadRequestWhenEndAtEqualsStartAt() {
        CreateEventRequest request = baseRequest();
        request.setStartAt(Instant.parse("2026-03-12T10:00:00Z"));
        request.setEndAt(Instant.parse("2026-03-12T10:00:00Z"));

        ApiException ex = assertThrows(ApiException.class, () -> eventService.createEvent(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldCreateEventSuccessfullyWithValidData() {
        CreateEventRequest request = baseRequest();
        request.setTitle("Valid Event");

        Event saved = eventFromRequest(request, 1);
        when(eventRepository.existsByOwnerIdAndTypeAndStartAtLessThanAndEndAtGreaterThan(
            request.getOwnerId(),
            EventType.APPOINTMENT,
            request.getEndAt(),
            request.getStartAt()
        )).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenReturn(saved);

        EventResponse result = eventService.createEvent(request);

        assertEquals(1, result.getId());
        assertEquals("Valid Event", result.getTitle());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldThrowConflictForOverlappingAppointment() {
        CreateEventRequest request = baseRequest();
        request.setTitle("New Appointment");

        when(eventRepository.existsByOwnerIdAndTypeAndStartAtLessThanAndEndAtGreaterThan(
            request.getOwnerId(),
            EventType.APPOINTMENT,
            request.getEndAt(),
            request.getStartAt()
        )).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> eventService.createEvent(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("Appointment overlaps with an existing appointment", ex.getMessage());
    }

    @Test
    void shouldDetectFullEnclosureWhenExistingAppointmentEnclosesNewOne() {
        CreateEventRequest request = baseRequest();
        request.setTitle("Small Event");
        request.setStartAt(Instant.parse("2026-03-12T10:00:00Z"));
        request.setEndAt(Instant.parse("2026-03-12T10:30:00Z"));

        when(eventRepository.existsByOwnerIdAndTypeAndStartAtLessThanAndEndAtGreaterThan(
            request.getOwnerId(),
            EventType.APPOINTMENT,
            request.getEndAt(),
            request.getStartAt()
        )).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> eventService.createEvent(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void shouldDetectFullEnclosureWhenNewAppointmentEnclosesExistingOne() {
        CreateEventRequest request = baseRequest();
        request.setTitle("Large Event");
        request.setStartAt(Instant.parse("2026-03-12T09:00:00Z"));
        request.setEndAt(Instant.parse("2026-03-12T12:00:00Z"));

        when(eventRepository.existsByOwnerIdAndTypeAndStartAtLessThanAndEndAtGreaterThan(
            request.getOwnerId(),
            EventType.APPOINTMENT,
            request.getEndAt(),
            request.getStartAt()
        )).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> eventService.createEvent(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void shouldAllowNonOverlappingAppointmentEvents() {
        CreateEventRequest request = baseRequest();
        request.setTitle("Non-overlapping");
        request.setStartAt(Instant.parse("2026-03-12T14:00:00Z"));
        request.setEndAt(Instant.parse("2026-03-12T15:00:00Z"));

        when(eventRepository.existsByOwnerIdAndTypeAndStartAtLessThanAndEndAtGreaterThan(
            request.getOwnerId(),
            EventType.APPOINTMENT,
            request.getEndAt(),
            request.getStartAt()
        )).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenReturn(eventFromRequest(request, 2));

        EventResponse result = eventService.createEvent(request);

        assertEquals(2, result.getId());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldAllowOverlappingBlockEventsWithoutOverlapCheck() {
        CreateEventRequest request = baseRequest();
        request.setTitle("Block Time");
        request.setType(EventType.BLOCK);

        when(eventRepository.save(any(Event.class))).thenReturn(eventFromRequest(request, 3));

        EventResponse result = eventService.createEvent(request);

        assertEquals(3, result.getId());
        verify(eventRepository, never()).existsByOwnerIdAndTypeAndStartAtLessThanAndEndAtGreaterThan(
            request.getOwnerId(),
            EventType.APPOINTMENT,
            request.getEndAt(),
            request.getStartAt()
        );
    }

    @Test
    void shouldReturnEventsOverlappingWithQueryRange() {
        Instant from = Instant.parse("2026-03-12T08:00:00Z");
        Instant to = Instant.parse("2026-03-12T13:00:00Z");
        Event event1 = event("Event 1", Instant.parse("2026-03-12T09:00:00Z"), Instant.parse("2026-03-12T10:00:00Z"), 1, 1);
        Event event2 = event("Event 2", Instant.parse("2026-03-12T11:00:00Z"), Instant.parse("2026-03-12T12:00:00Z"), 1, 2);

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(1, from, to))
            .thenReturn(List.of(event1, event2));

        List<EventResponse> result = eventService.getEvents(1, from, to);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Event 1", result.get(0).getTitle());
        assertEquals(1, result.get(0).getOwnerId());
        assertTrue(result.get(0).getStartAt() instanceof String);
        assertTrue(result.get(0).getEndAt() instanceof String);
        assertEquals(2, result.get(1).getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoEventsInRange() {
        Instant from = Instant.parse("2026-03-15T08:00:00Z");
        Instant to = Instant.parse("2026-03-15T17:00:00Z");

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(1, from, to))
            .thenReturn(List.of());

        List<EventResponse> result = eventService.getEvents(1, from, to);

        assertEquals(List.of(), result);
    }

    @Test
    void shouldFindPartiallyOverlappingEventsWhenEventStartsBeforeRange() {
        Instant from = Instant.parse("2026-03-12T09:00:00Z");
        Instant to = Instant.parse("2026-03-12T10:00:00Z");
        Event event = event("Partial Overlap", Instant.parse("2026-03-12T07:00:00Z"), Instant.parse("2026-03-12T09:30:00Z"), 1, 1);

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(1, from, to))
            .thenReturn(List.of(event));

        List<EventResponse> result = eventService.getEvents(1, from, to);

        assertEquals(1, result.size());
        assertEquals("Partial Overlap", result.get(0).getTitle());
        assertTrue(result.get(0).getStartAt() instanceof String);
    }

    @Test
    void shouldFindPartiallyOverlappingEventsWhenEventEndsAfterRange() {
        Instant from = Instant.parse("2026-03-12T09:00:00Z");
        Instant to = Instant.parse("2026-03-12T17:00:00Z");
        Event event = event("Partial Overlap End", Instant.parse("2026-03-12T16:00:00Z"), Instant.parse("2026-03-12T18:00:00Z"), 1, 1);

        when(eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(1, from, to))
            .thenReturn(List.of(event));

        List<EventResponse> result = eventService.getEvents(1, from, to);

        assertEquals(1, result.size());
        assertEquals("Partial Overlap End", result.get(0).getTitle());
        assertTrue(result.get(0).getStartAt() instanceof String);
    }

    private CreateEventRequest baseRequest() {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Interview");
        request.setStartAt(Instant.parse("2026-03-12T10:00:00Z"));
        request.setEndAt(Instant.parse("2026-03-12T11:00:00Z"));
        request.setTimezone("Asia/Ho_Chi_Minh");
        request.setType(EventType.APPOINTMENT);
        request.setOwnerId(1);
        return request;
    }

    private Event eventFromRequest(CreateEventRequest request, Integer id) {
        Event event = new Event();
        event.setId(id);
        event.setTitle(request.getTitle());
        event.setStartAt(request.getStartAt());
        event.setEndAt(request.getEndAt());
        event.setTimezone(request.getTimezone());
        event.setType(request.getType());
        event.setOwnerId(request.getOwnerId());
        return event;
    }

    private Event event(String title, Instant startAt, Instant endAt, Integer ownerId, Integer id) {
        Event event = new Event();
        event.setId(id);
        event.setTitle(title);
        event.setStartAt(startAt);
        event.setEndAt(endAt);
        event.setTimezone("UTC");
        event.setType(EventType.APPOINTMENT);
        event.setOwnerId(ownerId);
        return event;
    }
}
