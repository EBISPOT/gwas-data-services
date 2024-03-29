package uk.ac.ebi.spot.gwas.data.copy.table.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.EfoTrait;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface EfoTraitMongoRepository extends MongoRepository<EfoTrait, String> {

    Page<EfoTrait> findByTraitContainingIgnoreCase(String trait, Pageable page);
    List<EfoTrait> findByTraitContainingIgnoreCase(String trait);
    List<EfoTrait> findByTraitIgnoreCase(String trait);
    List<EfoTrait> findByUri(String uri);
    Optional<EfoTrait> findByShortForm(String shortForm);
    Stream<EfoTrait> findByShortFormIn(Set<String> shortForms);
}