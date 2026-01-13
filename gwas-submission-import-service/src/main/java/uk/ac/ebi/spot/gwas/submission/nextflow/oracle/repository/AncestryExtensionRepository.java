package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.AncestryExtension;

public interface AncestryExtensionRepository extends JpaRepository<AncestryExtension, Long> {
}
