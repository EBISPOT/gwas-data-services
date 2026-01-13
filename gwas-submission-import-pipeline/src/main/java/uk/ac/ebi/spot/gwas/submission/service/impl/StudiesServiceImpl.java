package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.submission.service.StudiesService;

@Service
public class StudiesServiceImpl implements StudiesService {

    StudyRepository studyRepository;

    public StudiesServiceImpl(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public Page<Study> findBySubmissionId(String submissionId, Pageable pageable) {
        return studyRepository.findBySubmissionId(submissionId, pageable);
    }

    public Long findBySubmissionId(String submissionId) {
        return studyRepository.findBySubmissionId(submissionId).count();
    }

    public Boolean checkSumstatsExists(String submissionId) {
        Long totalStudies = findBySubmissionId(submissionId);
        Long bucketStudies = totalStudies / 1000;
        for (int i = 0; i <= bucketStudies; i++) {
            Pageable pageable = PageRequest.of(i, 1000);
            Page<Study> studies = findBySubmissionId(submissionId, pageable);
            for (Study study : studies) {
                if (study.getSummaryStatisticsFile() != null && !study.getSummaryStatisticsFile().isEmpty()
                        && study.getSummaryStatisticsFile().equals("NR")) {
                    return true;
                }
            }
        }
        return false;
    }

}