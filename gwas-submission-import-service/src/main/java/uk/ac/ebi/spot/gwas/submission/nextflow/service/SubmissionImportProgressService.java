package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import java.util.List;

public interface SubmissionImportProgressService {


    Integer importSubmission(String submissionId,
                          List<String> studyIds,
                          String curatorEmail,
                          String pmid);

    Integer publishSummaryStats(String submissionId,
                            List<String> studyIds,
                            String pmid);


    void savePmidReporting(String submissionId, Integer studiesImported);
}
