package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.submission.constants.SubmissionType;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.SubmissionRepository;

public interface SubmissionService {

  Submission findById(String submissionId);

  SubmissionType getSubmissionType(Submission submission);
}
