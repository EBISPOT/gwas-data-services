package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Platform;

import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

   Optional<Platform> findByManufacturer(String manufacturer);
}
