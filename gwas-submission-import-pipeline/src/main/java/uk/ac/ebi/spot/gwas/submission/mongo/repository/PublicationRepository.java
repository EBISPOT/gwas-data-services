package uk.ac.ebi.spot.gwas.submission.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Publication;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;

import java.util.Optional;

public interface PublicationRepository extends MongoRepository<Publication, String> {

    Optional<Publication> findById(String publicationId);
}
