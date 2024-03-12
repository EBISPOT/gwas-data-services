package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.Event;

import java.util.List;

/**
 * Created by emma on 06/05/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Event entity object
 */

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserId(Long id);
}

