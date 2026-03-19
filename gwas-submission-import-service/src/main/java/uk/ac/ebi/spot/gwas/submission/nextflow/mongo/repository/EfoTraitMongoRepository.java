package uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.EfoTrait;

public interface EfoTraitMongoRepository extends MongoRepository<EfoTrait, String> {
}
