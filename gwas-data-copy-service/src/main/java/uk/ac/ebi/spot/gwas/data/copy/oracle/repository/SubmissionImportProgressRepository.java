package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.SubmissionImportProgress;

import java.util.Optional;


public interface SubmissionImportProgressRepository extends JpaRepository<SubmissionImportProgress, Long> {

    Optional<SubmissionImportProgress> findBySubmissionId(String submissionId);

}
