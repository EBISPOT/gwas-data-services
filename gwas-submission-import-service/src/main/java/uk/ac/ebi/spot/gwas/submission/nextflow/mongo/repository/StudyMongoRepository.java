package uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;

import java.util.List;
import java.util.Optional;

public interface StudyMongoRepository extends MongoRepository<Study , String> {

    Optional<Study> findById(String studyId);

    Long countBySubmissionId(String submissionId);

    Page<Study> findBySubmissionId(String submissionId, Pageable pageable);

    List<Study> findByAccession(String accession);
}
