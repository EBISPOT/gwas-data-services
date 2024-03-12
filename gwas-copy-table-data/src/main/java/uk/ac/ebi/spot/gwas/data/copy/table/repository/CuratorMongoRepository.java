package uk.ac.ebi.spot.gwas.data.copy.table.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Curator;

import java.util.Optional;

public interface CuratorMongoRepository extends MongoRepository<Curator, String> {

    Optional<Curator> findById(String id);

    Optional<Curator> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    Optional<Curator> findByLastNameIgnoreCase(String lastName);

    Optional<Curator> findByEmailIgnoreCase(String email);
}
