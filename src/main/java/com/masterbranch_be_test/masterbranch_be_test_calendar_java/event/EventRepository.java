package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {

    boolean existsByOwnerIdAndTypeAndStartAtLessThanAndEndAtGreaterThan(
        Integer ownerId,
        EventType type,
        Instant endAt,
        Instant startAt
    );

    List<Event> findByOwnerIdAndEndAtGreaterThanAndStartAtLessThanOrderByStartAtAsc(
        Integer ownerId,
        Instant from,
        Instant to
    );
}
