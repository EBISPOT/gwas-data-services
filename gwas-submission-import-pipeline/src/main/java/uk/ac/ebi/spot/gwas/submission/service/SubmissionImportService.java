package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.SubmissionRabbitMessage;

public interface SubmissionImportService {

    void importSubmission(SubmissionRabbitMessage submissionRabbitMessage);
}
