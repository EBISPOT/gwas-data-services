package uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Curator;

import java.util.Optional;

public interface CuratorMongoRepository extends MongoRepository<Curator, String> {

   Optional<Curator> findByEmail(String email);
}
