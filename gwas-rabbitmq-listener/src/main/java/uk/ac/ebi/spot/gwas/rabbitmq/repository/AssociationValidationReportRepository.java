package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.AssociationValidationReport;

import java.util.List;

/**
 * Created by emma on 24/06/2016.
 *
 * @author emma
 *         <p>
 *         Repository accessing Association Validation Report entity objects
 */

public interface AssociationValidationReportRepository extends JpaRepository<AssociationValidationReport, Long> {

    List<AssociationValidationReport> findByAssociationId(Long id);
}
