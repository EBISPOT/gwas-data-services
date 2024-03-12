package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.ac.ebi.spot.gwas.data.copy.model.Author;


public interface AuthorRepository extends JpaRepository<Author, Long> {


    Author findByFullname(String fullname);


    Author findByFullnameAndFirstNameAndLastNameAndInitialsAndAffiliation(String fullname, String firstName, String lastName,
                                                                    String initial, String affiliation);

}

