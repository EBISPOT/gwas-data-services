package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.CurationStatus;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Repository to access CurationStatus entity object
 */

public interface CurationStatusRepository extends JpaRepository<CurationStatus, Long> {

    CurationStatus findByStatus(String status);
}
