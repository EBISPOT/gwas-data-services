package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Housekeeping;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Housekeeping entity object
 */


public interface HousekeepingRepository extends JpaRepository<Housekeeping, Long> {

}
