package uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Sample;

import java.util.List;

public interface SampleMongoRepository extends MongoRepository<Sample, String> {

 List<Sample> findBySubmissionIdAndStudyTag(String submissionId , String studyTag);
}
