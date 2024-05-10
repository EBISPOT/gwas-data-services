package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Locus;

/**
 * Created by emma on 27/01/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing Locus entity object
 */

public interface LocusRepository extends JpaRepository<Locus, Long> {
}
