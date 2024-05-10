package uk.ac.ebi.spot.gwas.rabbitmq.service;

import uk.ac.ebi.spot.gwas.model.Author;
import uk.ac.ebi.spot.gwas.model.Publication;

public interface AuthorService {

    Author findUniqueAuthor(String fullname, String firstName, String lastName,
                            String initial, String affiliation);

    void addPublication(Author author, Publication publication, Integer sort);

}
