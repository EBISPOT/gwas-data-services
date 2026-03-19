package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.EfoTrait;

import java.util.Optional;

public interface EfoTraitRepository extends JpaRepository<EfoTrait, Long> {

        Optional<EfoTrait> findByShortForm(String shortForm);
}
