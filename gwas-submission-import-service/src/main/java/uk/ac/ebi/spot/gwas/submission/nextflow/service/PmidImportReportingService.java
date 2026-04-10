package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.PmidImportReporting;

public interface PmidImportReportingService {

   PmidImportReporting findBySubmissionId(String submissionId);

   void savePmidReporting(String submissionId, Integer studiesImported);
}
