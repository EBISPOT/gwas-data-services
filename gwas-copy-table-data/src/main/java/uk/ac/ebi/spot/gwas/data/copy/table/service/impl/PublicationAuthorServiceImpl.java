package uk.ac.ebi.spot.gwas.data.copy.table.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.data.copy.table.repository.PublicationAuthorRepository;
import uk.ac.ebi.spot.gwas.data.copy.table.service.PublicationAuthorService;
import uk.ac.ebi.spot.gwas.deposition.domain.PublicationAuthor;

import java.util.Optional;

@Service
public class PublicationAuthorServiceImpl implements PublicationAuthorService {

    @Autowired
    PublicationAuthorRepository publicationAuthorRepository;

    @Override
    public Optional<PublicationAuthor> findUniqueAuthor(String fullname, String firstName, String lastName, String initials, String affiliation) {
       return   publicationAuthorRepository.findByFullNameAndFirstNameAndLastNameAndInitialsAndAffiliation(fullname
               , firstName, lastName, initials, affiliation);
    }
}
