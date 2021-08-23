package uk.ac.ebi.spot.gwas.service.data;

import uk.ac.ebi.spot.gwas.model.SecureUser;
import uk.ac.ebi.spot.gwas.model.Trackable;

public interface TrackingOperationService {

    void create(Trackable trackable, SecureUser secureUser);

    void delete(Trackable trackable, SecureUser secureUser);

    void update(Trackable trackable, SecureUser secureUser, String eventType);

    void update(Trackable trackable, SecureUser secureUser, String eventType, String updateDescription);

}
