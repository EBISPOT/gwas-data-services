package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.AncestralGroup;

import java.util.Optional;

public interface AncestralGroupRepository extends JpaRepository<AncestralGroup, Long> {

  Optional<AncestralGroup> findByAncestralGroup(String ancestralGroup);
}
