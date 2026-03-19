package uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.DiseaseTrait;

import java.util.Optional;

public interface DiseaseTraitMongoRepository extends MongoRepository<DiseaseTrait, Long> {

  Optional<DiseaseTrait> findById(String id);
}
