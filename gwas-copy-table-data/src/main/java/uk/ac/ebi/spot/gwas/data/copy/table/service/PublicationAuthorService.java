package uk.ac.ebi.spot.gwas.data.copy.table.service;

import uk.ac.ebi.spot.gwas.deposition.domain.PublicationAuthor;

import java.util.Optional;

public interface PublicationAuthorService {

   Optional<PublicationAuthor> findUniqueAuthor(String fullname, String firstName, String lastName,
                                                String initial, String affiliation);
}
