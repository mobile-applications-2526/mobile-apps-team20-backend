package com.mbproyect.campusconnect.events.impl.event;

import com.mbproyect.campusconnect.events.contract.event.EventEventsNotifier;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventOrganiser;
import com.mbproyect.campusconnect.model.entity.event.EventParticipant;
import com.mbproyect.campusconnect.shared.service.MailService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Component
public class EventEventsNotifierImpl implements EventEventsNotifier {

    private final MailService mailService;

    public EventEventsNotifierImpl(MailService mailService) {
        this.mailService = mailService;
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
         return date.format(formatter);
    }

    private String eventTemplate(Event event, String mainAction) {
        String name = event.getName() != null ? event.getName() : "Unnamed event";
        String location = event.getLocation() != null
                ? event.getLocation().getPlaceName() + ", " + event.getLocation().getCity()
                : "Location not specified";
        String organiser = event.getOrganiser() != null && event.getOrganiser().getUserProfile() != null
                ? event.getOrganiser().getUserProfile().getUserName()
                : "Unknown organiser";

        return mainAction + ":\n" +
                "\nName: " + name +
                "\nLocation: " + location +
                "\nDate: " + formatDate(event.getStartDate()) + " - " + formatDate(event.getEndDate()) +
                "\nOrganiser: " + organiser;
    }


    // Method who calls the email service for each subscriber
    private void notifyParticipants(
            String subject,
            String content,
            Set<EventParticipant> participants
    ) {
        participants.forEach(
                (participant) -> mailService.sendEmail(participant.getEmail(), subject, content)
        );
    }

    private void notifyOrganiser(String subject, String content, EventOrganiser organiser) {
        mailService.sendEmail(organiser.getEmail(), subject, content);
    }

    @Override
    public void onEventChanged (
            Event event,
            List<String> changed,
            List<String> original
    ) {
        StringBuilder diff = new StringBuilder();
        for (int i = 0; i < changed.size(); i++) {
                   diff.append(original.get(i))
                    .append("</b> → <b>")
                    .append(changed.get(i));
        }

        String subject = "Event details updated";
        String body = "Some details of your event have changed: \n" + diff;
        notifyOrganiser(subject, body, event.getOrganiser());
        notifyParticipants(subject, body, event.getParticipants());
    }

    @Override
    public void onEventCancelled(Event event) {
        String mailContent = eventTemplate(event, "The following event has been cancelled");
        String mailSubject = event.getName() + " was cancelled";
        notifyOrganiser(mailSubject, mailContent, event.getOrganiser());
        notifyParticipants(mailSubject, mailContent, event.getParticipants());
    }

    @Override
    public void onParticipantSubscribed(Event event, EventParticipant participant) {
        String mailContent = eventTemplate(
                event, "You have been subscribed for the following event"
        );
        String mailSubject = "Subscription to " + event.getName();
        notifyParticipants(mailSubject, mailContent, Set.of(participant));
    }

    @Override
    public void onParticipantUnsubscribed(Event event, EventParticipant participant) {
        String mailContent = eventTemplate(
                event, "You have cancelled the subscription for the following event"
        );
        String mailSubject = "Subscription cancellation to " + event.getName();
        notifyParticipants(mailSubject, mailContent, Set.of(participant));
    }
}
