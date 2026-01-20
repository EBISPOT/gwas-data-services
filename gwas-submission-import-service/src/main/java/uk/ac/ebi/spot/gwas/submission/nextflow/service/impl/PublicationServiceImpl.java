package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.PublicationRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.PublicationService;

@Slf4j
@Service
public class PublicationServiceImpl implements PublicationService {

    PublicationRepository publicationRepository;

    public PublicationServiceImpl(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    //@Transactional(readOnly = true)
    public Publication findByPmid(String pmid) {
        log.info("Pmid is {}",pmid);
        return publicationRepository.findByPubmedId(pmid).orElse(null);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public void save(Publication publication) {
         publicationRepository.save(publication);
    }

}
