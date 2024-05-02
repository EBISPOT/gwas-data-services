package uk.ac.ebi.spot.gwas.rabbitmq.service;


import uk.ac.ebi.spot.gwas.model.Publication;

public interface PublicationService {

    void save(Publication publication);

    Publication findByPmid(String pmid);
}
