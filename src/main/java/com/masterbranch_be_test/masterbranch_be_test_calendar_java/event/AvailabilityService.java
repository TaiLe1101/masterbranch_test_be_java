package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRulesException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.AvailabilityQueryRequest;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.TimeSlotResponse;
import com.masterbranch_be_test.masterbranch_be_test_calendar_java.exception.ApiException;

@Service
public class AvailabilityService {

    private final EventRepository eventRepository;

    public AvailabilityService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<TimeSlotResponse> queryAvailability(AvailabilityQueryRequest request) {
        if (!request.getTo().isAfter(request.getFrom())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "to must be greater than from");
        }

        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(request.getTimezone());
        } catch (ZoneRulesException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid timezone");
        }

        if (!request.getWorkingHours().getEnd().isAfter(request.getWorkingHours().getStart())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "workingHours.end must be greater than workingHours.start");
        }

        ZonedDateTime queryFrom = request.getFrom().atZone(zoneId);
        ZonedDateTime queryTo = request.getTo().atZone(zoneId);

        List<Event> events = eventRepository.findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(
            request.getOwnerId(),
            request.getFrom(),
            request.getTo()
        );

        LocalDate currentDate = queryFrom.toLocalDate();
        LocalDate lastDate = queryTo.toLocalDate();

        List<TimeSlotResponse> slots = new ArrayList<>();

        while (!currentDate.isAfter(lastDate)) {
            ZonedDateTime workStartDateTime = currentDate.atTime(request.getWorkingHours().getStart()).atZone(zoneId);
            ZonedDateTime workEndDateTime = currentDate.atTime(request.getWorkingHours().getEnd()).atZone(zoneId);

            ZonedDateTime effectiveStart = workStartDateTime.isBefore(queryFrom) ? queryFrom : workStartDateTime;
            ZonedDateTime effectiveEnd = workEndDateTime.isAfter(queryTo) ? queryTo : workEndDateTime;

            ZonedDateTime cursor = effectiveStart;

            while (!cursor.plusMinutes(request.getSlotDuration()).isAfter(effectiveEnd)) {
                ZonedDateTime candidateEnd = cursor.plusMinutes(request.getSlotDuration());
                Instant candidateStartInstant = cursor.toInstant();
                Instant candidateEndInstant = candidateEnd.toInstant();

                boolean overlap = events.stream().anyMatch(event -> TimeRangeUtils.isOverlap(
                    event.getStartAt(),
                    event.getEndAt(),
                    candidateStartInstant,
                    candidateEndInstant
                ));

                if (!overlap) {
                    slots.add(new TimeSlotResponse(candidateStartInstant, candidateEndInstant));
                }

                cursor = candidateEnd;
            }

            currentDate = currentDate.plusDays(1);
        }

        return slots;
    }
}
