package uk.ac.ebi.spot.gwas.data.copy.table.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.PublicationAuthor;

import java.util.Optional;

public interface PublicationAuthorRepository extends MongoRepository<PublicationAuthor, String> {

    Optional<PublicationAuthor> findByFullNameAndFirstNameAndLastNameAndInitialsAndAffiliation(
            String fullName, String firstName, String lastName, String initials, String affiliation);
}
