package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.CurationStatus;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.model.Housekeeping;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.CurationStatusRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.CuratorRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.HousekeepingRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.HousekeepingService;

import java.util.Date;

@Service
public class HousekeepingServiceImpl implements HousekeepingService {

    HousekeepingRepository housekeepingRepository;

    CuratorRepository curatorRepository;

    CurationStatusRepository curationStatusRepository;


    public HousekeepingServiceImpl(HousekeepingRepository housekeepingRepository,
                                   CuratorRepository curatorRepository,
                                   CurationStatusRepository curationStatusRepository) {
        this.housekeepingRepository = housekeepingRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
    }

    public Housekeeping createHousekeeping() {

        Housekeeping housekeeping = new Housekeeping();
        Date studyAddedDate = new Date();
        housekeeping.setStudyAddedDate(studyAddedDate);
        CurationStatus curationStatus = curationStatusRepository.findBYStatus("Awaiting Curation").orElse(null);
        housekeeping.setCurationStatus(curationStatus);

        Curator curator = curatorRepository.findByLastName("Level 1 Curator").orElse(null);
        housekeeping.setCurator(curator);
        housekeepingRepository.save(housekeeping);
        return housekeeping;
    }
}
