package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.PmidImportReporting;

import java.util.List;

public interface PmidReportingService {

    PmidImportReporting findBySubmissionId(String submissionId);

    List<PmidImportReporting> findByStatus(String status);

    PmidImportReporting save(PmidImportReporting pmidImportReporting);

    void save(String submissionId, String status);
}
