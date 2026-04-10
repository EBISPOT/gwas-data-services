package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Submission;

public interface SubmissionService {

   Submission findBySubmissionId(String submissionId);
}
