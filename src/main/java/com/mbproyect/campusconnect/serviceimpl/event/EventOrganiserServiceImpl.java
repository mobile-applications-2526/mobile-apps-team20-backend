package com.mbproyect.campusconnect.serviceimpl.event;

import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.infrastructure.repository.event.EventOrganiserRepository;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventOrganiser;
import com.mbproyect.campusconnect.model.entity.user.User;
import com.mbproyect.campusconnect.service.event.EventOrganiserService;
import com.mbproyect.campusconnect.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class EventOrganiserServiceImpl implements EventOrganiserService {

    private final EventOrganiserRepository eventOrganiserRepository;
    private final UserService userService;

    public EventOrganiserServiceImpl(EventOrganiserRepository eventOrganiserRepository, UserService userService) {
        this.eventOrganiserRepository = eventOrganiserRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    @Override
    public EventOrganiser getEventOrganiserByEmail(String email, Event event) {
        EventOrganiser organiser = eventOrganiserRepository.findByEmail(email);

        if (organiser == null) {
            // If it doesn't exist yet
            organiser = createEventOrganiser(email, event);
        }

        return organiser;
    }

    @Transactional
    @Override
    public EventOrganiser createEventOrganiser(String email, Event event) {
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var eventOrganiser = new EventOrganiser();
        eventOrganiser.setEmail(email);
        eventOrganiser.setEvents(Set.of(event));
        eventOrganiser.setUserProfile(user.getUserProfile());

        eventOrganiserRepository.save(eventOrganiser);
        return this.getEventOrganiserByEmail(email, event);
    }

}
