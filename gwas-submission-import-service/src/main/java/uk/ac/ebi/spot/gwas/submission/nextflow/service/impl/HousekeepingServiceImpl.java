package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.CurationStatus;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.model.Housekeeping;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.HousekeepingRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.CurationStatusService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.CuratorService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.HousekeepingService;

import java.util.Date;

@Service
public class HousekeepingServiceImpl implements HousekeepingService {

    HousekeepingRepository housekeepingRepository;

    CuratorService curatorService;

    CurationStatusService curationStatusService;


    public HousekeepingServiceImpl(HousekeepingRepository housekeepingRepository,
                                   CuratorService curatorService,
                                   CurationStatusService curationStatusService) {
        this.housekeepingRepository = housekeepingRepository;
        this.curatorService = curatorService;
        this.curationStatusService = curationStatusService;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Housekeeping createHousekeeping() {

        Housekeeping housekeeping = new Housekeeping();
        Date studyAddedDate = new Date();
        housekeeping.setStudyAddedDate(studyAddedDate);
        CurationStatus curationStatus = curationStatusService.findByStatus("Level 2 curation done");
        housekeeping.setCurationStatus(curationStatus);

        Curator curator = curatorService.findByLastName("Level 2 Curator");
        housekeeping.setCurator(curator);
        housekeepingRepository.save(housekeeping);
        return housekeeping;
    }
}
