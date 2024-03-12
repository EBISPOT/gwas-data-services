package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.table.model.SubmissionImportProgress;

import java.util.Optional;


public interface SubmissionImportProgressRepository extends JpaRepository<SubmissionImportProgress, Long> {

    Optional<SubmissionImportProgress> findBySubmissionId(String submissionId);

}
