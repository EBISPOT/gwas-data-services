package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.DeletedStudy;

/**
 * Created by emma on 31/05/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Deleted Study entity object
 */

public interface DeletedStudyRepository extends JpaRepository<DeletedStudy, Long> {
}

