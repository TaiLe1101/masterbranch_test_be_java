package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.CreateEventRequest;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.EventResponse;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.exception.ApiException;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventResponse createEvent(CreateEventRequest request) {
        validateRange(request.getStartAt(), request.getEndAt());

        if (request.getType() == EventType.APPOINTMENT) {
            boolean overlap = eventRepository.existsByOwnerIdAndTypeAndStartAtLessThanAndEndAtGreaterThan(
                request.getOwnerId(),
                EventType.APPOINTMENT,
                request.getEndAt(),
                request.getStartAt()
            );

            if (overlap) {
                throw new ApiException(HttpStatus.CONFLICT, "Appointment overlaps with an existing appointment");
            }
        }

        Event event = EventMapper.toEntity(request);
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toResponse(savedEvent);
    }

    public List<EventResponse> getEvents(Integer ownerId, Instant from, Instant to) {
        validateRange(from, to);

        return eventRepository
            .findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(ownerId, from, to)
            .stream()
            .map(EventMapper::toResponse)
            .toList();
    }

    private void validateRange(Instant startAt, Instant endAt) {
        if (!endAt.isAfter(startAt)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "endAt must be greater than startAt");
        }
    }
}
