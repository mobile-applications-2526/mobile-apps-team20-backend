package com.mbproyect.campusconnect.infrastructure.repository.event;

import com.mbproyect.campusconnect.model.entity.event.EventOrganiser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventOrganiserRepository extends JpaRepository<EventOrganiser, UUID> {
    EventOrganiser findByEmail(String email);
}
