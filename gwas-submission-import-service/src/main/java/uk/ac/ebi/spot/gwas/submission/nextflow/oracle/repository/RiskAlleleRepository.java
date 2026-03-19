package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.RiskAllele;

public interface RiskAlleleRepository extends JpaRepository<RiskAllele, Long> {

}
