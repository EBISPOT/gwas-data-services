package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.RiskAllele;

import java.util.List;

/**
 * Created by emma on 27/01/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing RiskAllele entity object
 */

public interface RiskAlleleRepository extends JpaRepository<RiskAllele, Long> {
    List<RiskAllele> findByRiskAlleleName(String riskAlleleName);
}
