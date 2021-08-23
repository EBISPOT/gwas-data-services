package uk.ac.ebi.spot.gwas.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Event;
import uk.ac.ebi.spot.gwas.model.SecureUser;
import uk.ac.ebi.spot.gwas.repository.EventRepository;

import java.sql.Date;

@Service
public class EventOperationsService {

    @Autowired
    private EventRepository eventRepository;

    public synchronized Event createEvent(String eventType, SecureUser user) {
        // Create and save event
        Event event = new Event();
        event.setEventDate(new Date(System.currentTimeMillis()));
        event.setEventType(eventType);
        event.setUser(user);
        eventRepository.save(event);
        return event;
    }

    public synchronized Event createEvent(String eventType, SecureUser user, String eventDescription) {
        // Create and save event
        Event event = new Event();
        event.setEventDate(new Date(System.currentTimeMillis()));
        event.setEventType(eventType);
        event.setUser(user);
        event.setEventDescription(eventDescription);
        eventRepository.save(event);
        return event;
    }
}
