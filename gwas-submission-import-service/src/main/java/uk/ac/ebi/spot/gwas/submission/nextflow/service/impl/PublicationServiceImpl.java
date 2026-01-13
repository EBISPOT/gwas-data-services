package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.PublicationRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.PublicationService;

@Service
public class PublicationServiceImpl implements PublicationService {

    PublicationRepository publicationRepository;

    public PublicationServiceImpl(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    @Transactional(readOnly = true)
    public Publication findByPmid(String pmid) {
        return publicationRepository.findByPubmedId(pmid).orElse(null);
    }


    public void save(Publication publication) {
         publicationRepository.save(publication);
    }

}
