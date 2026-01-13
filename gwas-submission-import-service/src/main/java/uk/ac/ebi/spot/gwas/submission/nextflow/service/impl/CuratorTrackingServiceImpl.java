package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.CuratorTrackingRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.CuratorTrackingService;

@Service
public class CuratorTrackingServiceImpl implements CuratorTrackingService {

    CuratorTrackingRepository curatorTrackingRepository;

    public CuratorTrackingServiceImpl(CuratorTrackingRepository curatorTrackingRepository) {
        this.curatorTrackingRepository = curatorTrackingRepository;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteCuratorTrackingHistory(Long studyId) {
        curatorTrackingRepository.deleteAll(curatorTrackingRepository.findByStudyId(studyId));
    }
}
