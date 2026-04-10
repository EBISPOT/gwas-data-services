package uk.ac.ebi.spot.gwas.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.model.mongo.CurationStatus;

import java.util.Optional;

public interface CurationStatusMongoRepository extends MongoRepository<CurationStatus, String> {
    
    Optional<CurationStatus> findByStatus(String status);
}
