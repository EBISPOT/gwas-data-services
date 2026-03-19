package uk.ac.ebi.spot.gwas.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.common.model.CurationStatus;
import uk.ac.ebi.spot.gwas.common.model.Curator;
import uk.ac.ebi.spot.gwas.common.model.Housekeeping;
import uk.ac.ebi.spot.gwas.common.model.Study;
import uk.ac.ebi.spot.gwas.common.repository.CurationStatusRepository;
import uk.ac.ebi.spot.gwas.common.repository.CuratorRepository;
import uk.ac.ebi.spot.gwas.common.repository.StudyRepository;

import java.util.Date;
import java.util.List;

@Service
public class StudiesService {

    StudyRepository studyRepository;

    CurationStatusRepository curationStatusRepository;

    CuratorRepository curatorRepository;

    public StudiesService(StudyRepository studyRepository,
                          CurationStatusRepository curationStatusRepository,
                          CuratorRepository curatorRepository) {
        this.studyRepository = studyRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.curatorRepository = curatorRepository;
    }

    @Transactional(readOnly = true)
    public List<Study> getStudies(List<Long> studyIds) {
        return studyRepository.findByIdIsIn(studyIds);
    }

    public void publishStudies(List<Study> studies) {
       CurationStatus curationStatus = curationStatusRepository.findByStatus("Publish study");
       Curator curator = curatorRepository.findByLastName("GWAS Catalog");
        studies.forEach(study -> {
         Housekeeping housekeeping = study.getHousekeeping();
            housekeeping.setCurationStatus(curationStatus);
            housekeeping.setCurator(curator);
            housekeeping.setIsPublished(true);
            housekeeping.setCatalogPublishDate(new Date());
            housekeeping.setLastUpdateDate(new Date());
            study.setHousekeeping(housekeeping);
            studyRepository.save(study);
        });
    }

}
