package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.CurationStatus;

import java.util.Optional;

public interface CurationStatusRepository extends JpaRepository<CurationStatus, Long> {

  Optional<CurationStatus> findByStatus(String curationStatus);
}
