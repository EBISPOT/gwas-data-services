package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Curator;

public interface SubmissionImportProgressService {

    Boolean checkSubmissionExists(String submissionId);

    void saveNewSubmission(String submissionId, Curator curator);
}
