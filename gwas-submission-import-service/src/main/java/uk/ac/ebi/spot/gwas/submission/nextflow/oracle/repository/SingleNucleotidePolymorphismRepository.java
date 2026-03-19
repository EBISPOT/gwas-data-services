package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;

import java.util.Optional;

public interface SingleNucleotidePolymorphismRepository extends JpaRepository<SingleNucleotidePolymorphism, Long> {

      Optional<SingleNucleotidePolymorphism> findByRsIdIgnoreCase(String rsId);
}
