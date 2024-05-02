package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.DeletedAssociation;

/**
 * Created by emma on 06/06/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Deleted Association entity object
 */

public interface DeletedAssociationRepository extends JpaRepository<DeletedAssociation, Long> {
}

