package uk.ac.ebi.spot.gwas.submission.service;


import uk.ac.ebi.spot.gwas.exception.SlurmProcessException;
import uk.ac.ebi.spot.gwas.model.PmidImportReporting;

public interface SubmissionImportService {


    void importSubmission(String submissionId, String curatorEmail) throws SlurmProcessException;

    void deleteSubmissionInProgressEntry(String submissionId);

    void savePmidReporting(String submissionId, String status);

    void savePmidReporting(PmidImportReporting pmidImportReporting);
}
