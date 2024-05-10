package uk.ac.ebi.spot.gwas.rabbitmq.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Author;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.model.PublicationAuthors;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.PublicationAuthorsRepository;
import uk.ac.ebi.spot.gwas.rabbitmq.service.PublicationAuthorsService;

import java.util.Optional;

@Service
public class PublicationAuthorsServiceImpl implements PublicationAuthorsService {
    @Autowired
    PublicationAuthorsRepository publicationAuthorsRepository;

    public void setSort(Author author, Publication publication, Integer sort) {
        PublicationAuthors entry = createOrFindByPrimaryKey(author, publication, sort);
        save(entry);
    }

    public void save(PublicationAuthors entry) {
        publicationAuthorsRepository.save(entry);
    }


    public PublicationAuthors createOrFindByPrimaryKey(Author author, Publication publication, Integer sort) {
        PublicationAuthors entry = findByPrimaryKey(author, publication, sort);
        if (entry == null) {
            entry = new PublicationAuthors(author,publication, sort);
        }
        return entry;
    }

    public PublicationAuthors findByPrimaryKey(Author author, Publication publication, Integer sort) {
        Optional<PublicationAuthors> entry = findOptionalByPrimaryKey(author, publication, sort);
        if (entry.isPresent()){
            return entry.get();
        }
        return null;
    }

    Optional<PublicationAuthors> findOptionalByPrimaryKey(Author author, Publication publication, Integer sort) {
        PublicationAuthors entry = publicationAuthorsRepository.findByAuthorIdAndPublicationIdAndSort(author.getId(),
                publication.getId(), sort);
        if (entry != null) {
            return Optional.of(entry);
        }
        return Optional.empty();
    }



}
