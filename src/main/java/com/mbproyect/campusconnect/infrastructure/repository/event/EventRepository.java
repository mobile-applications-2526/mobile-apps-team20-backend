package com.mbproyect.campusconnect.infrastructure.repository.event;

import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    Set<Event> findByLocation_City(String locationCity);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.startDate >= :date AND e.eventStatus = :status " +
            "ORDER BY e.startDate ASC")
    List<Event> getUpcomingEvents (@Param("date") LocalDateTime date, @Param("status") EventStatus status);

    @Query("""
        SELECT e FROM Event e
        JOIN e.eventBio b
        JOIN b.interestTags t
        WHERE t IN :tags
        AND e.eventStatus = :status
    """)
    Set<Event> getEventsByAnyTag(@Param("tags") Set<InterestTag> tags, @Param("status") EventStatus status);

    Event findByEventId(UUID eventId);
}
