package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.SubmissionImportProgress;

import java.util.Optional;


public interface SubmissionImportProgressRepository extends JpaRepository<SubmissionImportProgress, Long> {

    Optional<SubmissionImportProgress> findBySubmissionId(String submissionId);

}
