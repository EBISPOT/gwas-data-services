package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.DeletedAssociation;

/**
 * Created by emma on 06/06/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Deleted Association entity object
 */

public interface DeletedAssociationRepository extends JpaRepository<DeletedAssociation, Long> {
}

