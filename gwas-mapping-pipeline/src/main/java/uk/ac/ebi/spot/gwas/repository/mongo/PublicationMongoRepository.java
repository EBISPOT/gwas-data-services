package uk.ac.ebi.spot.gwas.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.model.mongo.Publication;

import java.util.Optional;

public interface PublicationMongoRepository extends MongoRepository<Publication, String> {

    Optional<Publication> findByPmid(String pmid);
}
