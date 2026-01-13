package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository.SubmissionRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SubmissionService;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    SubmissionRepository submissionRepository;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Transactional(readOnly = true)
    public Submission findBySubmissionId(String submissionId) {
        return submissionRepository.findById(submissionId).orElse(null);
    }
}
