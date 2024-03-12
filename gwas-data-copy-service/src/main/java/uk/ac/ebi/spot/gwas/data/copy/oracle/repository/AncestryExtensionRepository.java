package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.AncestryExtension;


public interface AncestryExtensionRepository extends JpaRepository<AncestryExtension, Long> {}
