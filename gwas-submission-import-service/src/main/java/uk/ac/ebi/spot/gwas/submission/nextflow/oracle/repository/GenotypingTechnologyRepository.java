package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.GenotypingTechnology;

import java.util.Optional;

public interface GenotypingTechnologyRepository extends JpaRepository<GenotypingTechnology, Long> {

  Optional<GenotypingTechnology> findByGenotypingTechnology(String genotypingTechnology);
}
