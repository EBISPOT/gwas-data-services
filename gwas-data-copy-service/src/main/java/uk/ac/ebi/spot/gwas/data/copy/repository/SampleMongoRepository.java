package uk.ac.ebi.spot.gwas.data.copy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Sample;

import java.util.List;
import java.util.stream.Stream;


public interface SampleMongoRepository extends MongoRepository<Sample, String> {

    Stream<Sample> readBySubmissionId(String submissionId);

    Page<Sample> findBySubmissionId(String submissionId, Pageable page);

    List<Sample> findByIdIn(List<String> ids);
}
