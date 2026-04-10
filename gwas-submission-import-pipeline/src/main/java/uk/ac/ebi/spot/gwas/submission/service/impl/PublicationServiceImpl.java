package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Publication;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.PublicationRepository;
import uk.ac.ebi.spot.gwas.submission.service.PublicationService;

@Service
public class PublicationServiceImpl implements PublicationService {

    PublicationRepository publicationRepository;


    public PublicationServiceImpl(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public Publication findByPublicationId(String pubId) {
      return  publicationRepository.findById(pubId).orElse(null);
    }
}
