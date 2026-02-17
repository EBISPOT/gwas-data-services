package uk.ac.ebi.spot.gwas.submission.mongo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Association;

import java.util.stream.Stream;

public interface AssociationRepository extends MongoRepository<Association, String> {

    Page<Association> findBySubmissionId(String submissionId, Pageable pageable);

    Stream<Association> findBySubmissionId(String submissionId);

    Long countAssociationsBySubmissionId(String submissionId);
}
