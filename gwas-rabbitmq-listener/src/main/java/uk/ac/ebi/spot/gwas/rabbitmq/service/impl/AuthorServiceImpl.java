package uk.ac.ebi.spot.gwas.rabbitmq.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Author;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.AuthorRepository;
import uk.ac.ebi.spot.gwas.rabbitmq.service.AuthorService;
import uk.ac.ebi.spot.gwas.rabbitmq.service.PublicationAuthorsService;

import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    PublicationAuthorsService publicationAuthorsService;

    public Author findUniqueAuthor(String fullname, String firstName, String lastName,
                                   String initial, String affiliation){
        Optional<Author> author= findOptionalUniqueAuthor(fullname, firstName, lastName, initial, affiliation);
        return (author.isPresent()) ? author.get() : null;

    }


    Optional<Author> findOptionalUniqueAuthor(String fullname, String firstName, String lastName,
                                                      String initial, String affiliation) {
        Author author = authorRepository.findByFullnameAndFirstNameAndLastNameAndInitialsAndAffiliation(fullname, firstName,
                lastName, initial, affiliation);
        return (author != null) ? Optional.of(author) : Optional.empty();
    }


    public void save(Author author) {
        authorRepository.save(author);
    }

    public void addPublication(Author author, Publication publication, Integer sort) {
        save(author);
        publicationAuthorsService.setSort(author, publication, sort);
    }
}
