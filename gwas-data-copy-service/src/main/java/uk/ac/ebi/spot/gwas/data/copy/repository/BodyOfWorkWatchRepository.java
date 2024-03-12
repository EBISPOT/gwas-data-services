package uk.ac.ebi.spot.gwas.data.copy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.BodyOfWorkWatch;

import java.util.Optional;

public interface BodyOfWorkWatchRepository extends MongoRepository<BodyOfWorkWatch, String> {

    Optional<BodyOfWorkWatch> findByBowId(String bowId);

}
