package uk.ac.ebi.spot.gwas.submission.mongo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;

import java.util.List;
import java.util.stream.Stream;

public interface StudyRepository extends MongoRepository<Study, String> {

  Page<Study> findBySubmissionId(String submissionId, Pageable pageable);

  Stream<Study> findBySubmissionId(String submissionId);
}
