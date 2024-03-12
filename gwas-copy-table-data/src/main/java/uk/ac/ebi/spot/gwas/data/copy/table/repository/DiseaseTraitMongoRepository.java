package uk.ac.ebi.spot.gwas.data.copy.table.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.DiseaseTrait;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface DiseaseTraitMongoRepository extends MongoRepository<DiseaseTrait, String> {

    Page<DiseaseTrait> findByTrait(String trait, Pageable page);

    Page<DiseaseTrait> findByTraitContainingIgnoreCase(String trait, Pageable page);

    Optional<DiseaseTrait> findByTraitIgnoreCase(String trait);

    Stream<DiseaseTrait> findByTraitIgnoreCaseIn(Set<String> traits);
}
