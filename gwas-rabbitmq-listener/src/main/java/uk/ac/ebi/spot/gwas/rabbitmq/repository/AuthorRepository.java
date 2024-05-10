package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {


    Author findByFullname(String fullname);


    Author findByFullnameAndFirstNameAndLastNameAndInitialsAndAffiliation(String fullname, String firstName, String lastName,
                                                                    String initial, String affiliation);

}

