package uk.ac.ebi.spot.gwas.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.model.mongo.Curator;

import java.util.Optional;

public interface CuratorRepository extends MongoRepository<Curator, String> {
    
    Optional<Curator> findByEmail(String email);
}
