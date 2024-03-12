package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.table.model.EventType;

/**
 * Created by Cinzia on 11/11/2016.
 *
 * @author Cinzia
 *         <p>
 *         Repository accessing EventType entity object
 */


public interface EventTypeRepository extends JpaRepository<EventType, Long> {

    EventType findByAction(String action);
    EventType findByEventType(String eventType);

}
