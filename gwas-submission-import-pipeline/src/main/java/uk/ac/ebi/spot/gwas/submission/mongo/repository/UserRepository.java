package uk.ac.ebi.spot.gwas.submission.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Long> {

    Optional<User> findById(String userId);

   Optional<User> findByEmailIgnoreCase(String email);

}
