package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Housekeeping;

public interface HousekeepingRepository extends JpaRepository<Housekeeping, Long> {
}
