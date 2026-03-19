package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.mongo.CurationStatus;
import uk.ac.ebi.spot.gwas.model.mongo.Curator;
import uk.ac.ebi.spot.gwas.model.mongo.Publication;
import uk.ac.ebi.spot.gwas.repository.mongo.CurationStatusMongoRepository;
import uk.ac.ebi.spot.gwas.repository.mongo.CuratorRepository;
import uk.ac.ebi.spot.gwas.repository.mongo.PublicationMongoRepository;
import uk.ac.ebi.spot.gwas.service.PublicationService;

@Slf4j
@Service
public class PublicationServiceImpl implements PublicationService {

    PublicationMongoRepository publicationRepository;

    CurationStatusMongoRepository curationStatusRepository;

    CuratorRepository curatorRepository;

    public PublicationServiceImpl(PublicationMongoRepository publicationRepository,
                                  CurationStatusMongoRepository curationStatusRepository,
                                  CuratorRepository curatorRepository) {
        this.publicationRepository = publicationRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.curatorRepository = curatorRepository;
    }

    public void updateCurationStatus(String pubmedId, String curatorEmail) {
        log.info("PubmedId status to be updated is {} and curator email is {} ", pubmedId, curatorEmail);
        Publication publication = publicationRepository.findByPmid(pubmedId).orElse(null);
        CurationStatus curationStatus = curationStatusRepository.findByStatus("Publish Study").orElse(null);
        Curator curator = curatorRepository.findByEmail(curatorEmail).orElse(null);
        if(publication != null) {
            publication.setCurationStatusId(curationStatus.getId());
            publication.setCuratorId(curator.getId());
            publicationRepository.save(publication);
        }
    }
}
