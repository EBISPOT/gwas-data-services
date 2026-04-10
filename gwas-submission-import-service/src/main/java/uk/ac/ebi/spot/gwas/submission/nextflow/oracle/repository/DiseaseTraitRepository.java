package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.DiseaseTrait;

import java.util.Optional;

public interface DiseaseTraitRepository extends JpaRepository<DiseaseTrait, Long> {

   Optional<DiseaseTrait> findByTrait(String trait);
}
