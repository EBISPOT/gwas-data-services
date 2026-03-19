package uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;

import java.util.Optional;

public interface SubmissionRepository extends MongoRepository<Submission, String> {

    Optional<Submission> findById(String submissionId);
}
