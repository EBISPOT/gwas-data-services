package uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Association;

import java.util.List;

public interface AssociationMongoRepository extends MongoRepository<Association, Long> {

 List<Association> findBySubmissionIdAndStudyTagIgnoreCase(String submissionId , String studyTag);
}
