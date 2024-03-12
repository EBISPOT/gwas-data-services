package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.table.model.AncestryExtension;


public interface AncestryExtensionRepository extends JpaRepository<AncestryExtension, Long> {}
