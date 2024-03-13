package uk.ac.ebi.spot.gwas.data.copy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.Curator;

import java.util.Optional;

public interface CuratorMongoRepository extends MongoRepository<Curator, String> {

    public Optional<Curator> findById(String id);
    public Optional<Curator> findByFirstNameAndLastName(String firstName, String lastName);
}
