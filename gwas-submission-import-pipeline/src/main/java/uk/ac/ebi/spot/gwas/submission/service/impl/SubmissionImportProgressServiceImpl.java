package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Curator;
import uk.ac.ebi.spot.gwas.model.SubmissionImportProgress;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.SubmissionImportProgressRepository;
import uk.ac.ebi.spot.gwas.submission.service.SubmissionImportProgressService;

import java.util.Calendar;
import java.util.Optional;

@Service
public class SubmissionImportProgressServiceImpl implements SubmissionImportProgressService {

    SubmissionImportProgressRepository submissionImportProgressRepository;

    public SubmissionImportProgressServiceImpl(SubmissionImportProgressRepository submissionImportProgressRepository) {
        this.submissionImportProgressRepository = submissionImportProgressRepository;
    }

    public Boolean checkSubmissionExists(String submissionId) {
       Optional<SubmissionImportProgress> optional =  submissionImportProgressRepository.findBySubmissionId(submissionId);
       return optional.isPresent();
    }


    public void saveNewSubmission(String submissionId, Curator curator) {
       SubmissionImportProgress submissionImportProgress = SubmissionImportProgress.builder().submissionId(submissionId)
                .userEmail(curator.getEmail())
                .timestamp(Calendar.getInstance().getTime())
                .build();
        submissionImportProgressRepository.save(submissionImportProgress);
    }

}
