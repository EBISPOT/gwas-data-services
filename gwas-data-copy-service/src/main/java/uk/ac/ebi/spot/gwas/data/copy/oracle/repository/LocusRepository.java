package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.Locus;

/**
 * Created by emma on 27/01/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing Locus entity object
 */

public interface LocusRepository extends JpaRepository<Locus, Long> {
}
