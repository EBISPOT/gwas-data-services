package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.model.PmidImportReporting;

import java.util.List;

public interface PmidImportReportingService {

   PmidImportReporting findBySubmissionId(String submissionId);

    List<PmidImportReporting> findByStatus(String status);

    void save(String submissionId, String status);

    void save(PmidImportReporting pmidImportReporting);
}
