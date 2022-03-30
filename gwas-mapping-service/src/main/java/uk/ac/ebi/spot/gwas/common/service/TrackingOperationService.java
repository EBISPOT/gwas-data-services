package uk.ac.ebi.spot.gwas.common.service;

import uk.ac.ebi.spot.gwas.common.model.SecureUser;
import uk.ac.ebi.spot.gwas.common.model.Trackable;

public interface TrackingOperationService {

    void create(Trackable trackable, SecureUser secureUser);

    void delete(Trackable trackable, SecureUser secureUser);

    void update(Trackable trackable, SecureUser secureUser, String eventType);

    void update(Trackable trackable, SecureUser secureUser, String eventType, String updateDescription);

}
