package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import java.util.List;

public interface SubmissionImportProgressService {

    void importSubmission(String submissionId, List<String> studyIds);

    void importSubmission(String submissionId,
                          List<String> studyIds,
                          String curatorEmail,
                          String pmid);
}
