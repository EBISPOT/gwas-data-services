package uk.ac.ebi.spot.gwas.rabbitmq.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.PublicationRepository;
import uk.ac.ebi.spot.gwas.rabbitmq.service.PublicationService;

@Service
public class PublicationServiceImpl implements PublicationService {
    @Autowired
    PublicationRepository publicationRepository;

    public void save(Publication publication) {
        publicationRepository.save(publication);
    }


    public Publication findByPmid(String pmid) {
        return publicationRepository.findByPubmedId(pmid);
    }
}
