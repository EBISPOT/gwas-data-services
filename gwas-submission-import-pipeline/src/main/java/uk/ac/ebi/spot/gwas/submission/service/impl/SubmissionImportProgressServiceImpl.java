package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Curator;
import uk.ac.ebi.spot.gwas.model.SubmissionImportProgress;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.SubmissionImportProgressRepository;
import uk.ac.ebi.spot.gwas.submission.service.CuratorService;
import uk.ac.ebi.spot.gwas.submission.service.SubmissionImportProgressService;

import java.util.Calendar;
import java.util.Optional;

@Service
public class SubmissionImportProgressServiceImpl implements SubmissionImportProgressService {

    SubmissionImportProgressRepository submissionImportProgressRepository;

    CuratorService curatorService;

    public SubmissionImportProgressServiceImpl(SubmissionImportProgressRepository submissionImportProgressRepository,
                                               CuratorService curatorService) {
        this.submissionImportProgressRepository = submissionImportProgressRepository;
        this.curatorService = curatorService;
    }

    public Boolean checkSubmissionExists(String submissionId) {
       Optional<SubmissionImportProgress> optional =  submissionImportProgressRepository.findBySubmissionId(submissionId);
       return optional.isPresent();
    }


    public void saveNewSubmission(String submissionId, String curatorEmail) {
       SubmissionImportProgress submissionImportProgress = SubmissionImportProgress.builder().submissionId(submissionId)
                .userEmail(curatorEmail)
                .timestamp(Calendar.getInstance().getTime())
                .build();
        submissionImportProgressRepository.save(submissionImportProgress);
    }

    public void deleteSubmissionInProgressEntry(String submissionId) {
        submissionImportProgressRepository.findBySubmissionId(submissionId)
                .ifPresent(submissionImportProgress -> {
                    submissionImportProgressRepository.delete(submissionImportProgress);
                } );
    }

}
