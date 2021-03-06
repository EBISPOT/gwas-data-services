package uk.ac.ebi.spot.gwas.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.common.model.Event;
import uk.ac.ebi.spot.gwas.common.model.SecureUser;
import uk.ac.ebi.spot.gwas.common.model.Trackable;

@Service
public class AssociationTrackingOperationServiceImpl implements TrackingOperationService {

    @Autowired
    private EventOperationsService eventOperationsService;

    @Override
    public void create(Trackable trackable, SecureUser secureUser) {
        Event creationEvent = eventOperationsService.createEvent("ASSOCIATION_CREATION", secureUser);
        trackable.addEvent(creationEvent);
    }

    @Override
    public void update(Trackable trackable, SecureUser secureUser, String eventType) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser);
        trackable.addEvent(updateEvent);
    }

    @Override
    public void update(Trackable trackable, SecureUser secureUser, String eventType, String description) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser, description);
        trackable.addEvent(updateEvent);
    }

    @Override
    public void delete(Trackable trackable, SecureUser secureUser) {
        Event deleteEvent = eventOperationsService.createEvent("ASSOCIATION_DELETION", secureUser);
        trackable.addEvent(deleteEvent);
    }
}
