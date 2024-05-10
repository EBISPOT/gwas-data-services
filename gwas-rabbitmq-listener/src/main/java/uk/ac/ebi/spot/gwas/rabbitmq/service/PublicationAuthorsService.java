package uk.ac.ebi.spot.gwas.rabbitmq.service;


import uk.ac.ebi.spot.gwas.model.Author;
import uk.ac.ebi.spot.gwas.model.Publication;

public interface PublicationAuthorsService {

    void setSort(Author author, Publication publication, Integer sort);
}
