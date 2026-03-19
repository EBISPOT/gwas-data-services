package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.CuratorTrackingRepository;
import uk.ac.ebi.spot.gwas.submission.service.CuratorTrackingService;

@Service
public class CuratorTrackingServiceImpl implements CuratorTrackingService {

    CuratorTrackingRepository curatorTrackingRepository;

    public CuratorTrackingServiceImpl(CuratorTrackingRepository curatorTrackingRepository) {
        this.curatorTrackingRepository = curatorTrackingRepository;
    }

    public void deleteCuratorTrackingHistory(Long studyId) {
        curatorTrackingRepository.deleteAll(curatorTrackingRepository.findByStudyId(studyId));
    }
}
