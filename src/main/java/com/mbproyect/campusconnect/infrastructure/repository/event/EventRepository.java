package com.mbproyect.campusconnect.infrastructure.repository.event;

import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("""
        SELECT e FROM Event e
        WHERE e.location.city = :city
        AND e.eventStatus = :status
        AND e.organiser.email <> :excludedEmail
    """)
    Page<Event> findByLocation_City(
            @Param("city") String locationCity,
            @Param("status") EventStatus status,
            @Param("excludedEmail") String excludedEmail,
            Pageable pageable
    );

    @Query("""
        SELECT e FROM Event e 
        WHERE e.startDate >= :date 
        AND e.eventStatus = :status 
        AND e.organiser.email <> :excludedEmail
        ORDER BY e.startDate ASC
    """)
    Page<Event> getUpcomingEvents (
            @Param("date") LocalDateTime date,
            @Param("status") EventStatus status,
            @Param("excludedEmail") String excludedEmail,
            Pageable pageable
    );

    @Query("""
        SELECT e FROM Event e
        JOIN e.eventBio b
        JOIN b.interestTags t
        WHERE t IN :tags
        AND e.eventStatus = :status
        AND e.organiser.email <> :excludedEmail
    """)
    Page<Event> getEventsByAnyTag(
            @Param("tags") Set<InterestTag> tags,
            @Param("status") EventStatus status,
            @Param("excludedEmail") String excludedEmail,
            Pageable pageable
    );

    @Query("""
        SELECT e FROM Event e
        WHERE e.eventId = :id and e.eventStatus = :status
    """)
    Event findByEventId(
            @Param("id") UUID eventId,
            @Param("status") EventStatus status
    );

    @Query("""
        SELECT e FROM Event e
        WHERE e.organiser.userProfile.id = :profileId and e.eventStatus = :status
        ORDER BY e.startDate ASC
    """)
    Page<Event> findEventsByCreator(
            @Param("profileId") UUID profileId,
            @Param("status") EventStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT e FROM Event e
        JOIN e.eventBio b
        JOIN b.interestTags t
        WHERE e.location.city = :city
        AND t IN :tags
        AND e.eventStatus = :status
        AND e.organiser.email <> :excludedEmail
    """)
    Page<Event> findByLocation_CityAndEventBio_InterestTags(
            @Param("city") String locationCity,
            @Param("tags") Set<InterestTag> eventBioInterestTags,
            @Param("status") EventStatus status,
            @Param("excludedEmail") String excludedEmail,
            Pageable pageable
    );

    @Query("""
        SELECT e FROM Event e
        JOIN e.eventBio b
        JOIN b.interestTags t
        WHERE e.startDate = :date
        AND t IN :tags
        AND e.eventStatus = :status
        AND e.organiser.email <> :excludedEmail
    """)
    Page<Event> findByStartDateAndEventBio_InterestTags(
            @Param("date") LocalDateTime startDate,
            @Param("tags") Set<InterestTag> eventBioInterestTags,
            @Param("status") EventStatus status,
            @Param("excludedEmail") String excludedEmail,
            Pageable pageable
    );

    @Query("""
        SELECT COUNT(e) > 0
        FROM Event e
        LEFT JOIN e.participants p
        WHERE e.eventId = :eventId
        AND e.eventStatus = :status
        AND (e.organiser.email = :email OR p.email = :email)
    """)
    boolean isUserInEvent(
            @Param("email") String email,
            @Param("eventId") UUID eventId,
            @Param("status") EventStatus status
    );

}